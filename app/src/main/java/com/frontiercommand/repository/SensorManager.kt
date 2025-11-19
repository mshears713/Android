package com.frontiercommand.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow

/**
 * SensorManager - GPS and location services manager
 *
 * Wraps Android's FusedLocationProviderClient to provide reactive GPS data
 * access with proper permission handling and lifecycle management.
 *
 * **Features:**
 * - Runtime permission checking
 * - Reactive location updates via StateFlow
 * - High-accuracy location requests
 * - Automatic error handling
 * - Permission state tracking
 *
 * **Permission Flow:**
 * 1. Check if location permission granted
 * 2. If not, emit PermissionDenied state
 * 3. If granted, start location updates
 * 4. Emit location data via StateFlow
 *
 * **Usage:**
 * ```kotlin
 * val sensorManager = SensorManager(context)
 *
 * // Check permission
 * if (sensorManager.hasLocationPermission()) {
 *     // Start updates
 *     sensorManager.startLocationUpdates()
 *
 *     // Collect location
 *     sensorManager.locationFlow.collect { result ->
 *         when (result) {
 *             is LocationResult.Success -> {
 *                 val location = result.location
 *                 Log.d("GPS", "Lat: ${location.latitude}, Lng: ${location.longitude}")
 *             }
 *             is LocationResult.Error -> {
 *                 Log.e("GPS", "Error: ${result.message}")
 *             }
 *             LocationResult.PermissionDenied -> {
 *                 // Request permission from user
 *             }
 *         }
 *     }
 * }
 *
 * // Stop updates when done
 * sensorManager.stopLocationUpdates()
 * ```
 *
 * @param context Application or Activity context
 */
class SensorManager(private val context: Context) {

    private val TAG = "SensorManager"

    /**
     * Fused Location Provider Client from Google Play Services
     * Provides battery-efficient location updates
     */
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Location request configuration
     * - High accuracy GPS
     * - 5 second update interval
     * - 2 second fastest interval
     */
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        5000L // 5 seconds
    ).apply {
        setMinUpdateIntervalMillis(2000L) // 2 seconds
        setMaxUpdateDelayMillis(10000L) // 10 seconds
    }.build()

    /**
     * Callback for location updates
     */
    private var locationCallback: LocationCallback? = null

    /**
     * Internal mutable location flow
     */
    private val _locationFlow = MutableStateFlow<LocationResult>(LocationResult.Idle)

    /**
     * Public read-only location flow
     * UI can collect this to receive location updates
     */
    val locationFlow: StateFlow<LocationResult> = _locationFlow.asStateFlow()

    /**
     * Checks if the app has location permissions
     *
     * @return true if both FINE and COARSE location permissions granted
     */
    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    /**
     * Starts receiving location updates
     *
     * Requires location permission. If permission not granted, emits PermissionDenied.
     *
     * @throws SecurityException if permissions not granted (caught internally)
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        try {
            // Check permission
            if (!hasLocationPermission()) {
                Log.w(TAG, "Location permission not granted")
                _locationFlow.value = LocationResult.PermissionDenied
                return
            }

            Log.d(TAG, "Starting location updates...")

            // Create callback if not exists
            if (locationCallback == null) {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                        result.lastLocation?.let { location ->
                            Log.d(TAG, "Location update: ${location.latitude}, ${location.longitude}")
                            _locationFlow.value = LocationResult.Success(location)
                        }
                    }

                    override fun onLocationAvailability(availability: LocationAvailability) {
                        if (!availability.isLocationAvailable) {
                            Log.w(TAG, "Location not available (GPS may be disabled)")
                            _locationFlow.value = LocationResult.Error("GPS is disabled. Please enable location services.")
                        }
                    }
                }
            }

            // Request location updates
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            ).addOnSuccessListener {
                Log.i(TAG, "Location updates started successfully")
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to start location updates", e)
                _locationFlow.value = LocationResult.Error("Failed to start location updates: ${e.message}")
            }

        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception starting location updates", e)
            _locationFlow.value = LocationResult.PermissionDenied
        } catch (e: Exception) {
            Log.e(TAG, "Error starting location updates", e)
            _locationFlow.value = LocationResult.Error("Error: ${e.message}")
        }
    }

    /**
     * Stops receiving location updates
     *
     * Call this when location updates are no longer needed to save battery.
     */
    fun stopLocationUpdates() {
        try {
            locationCallback?.let { callback ->
                Log.d(TAG, "Stopping location updates...")
                fusedLocationClient.removeLocationUpdates(callback)
                    .addOnSuccessListener {
                        Log.i(TAG, "Location updates stopped successfully")
                        _locationFlow.value = LocationResult.Idle
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to stop location updates", e)
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping location updates", e)
        }
    }

    /**
     * Gets the last known location (cached)
     *
     * Returns immediately with last location, may be null if no location cached.
     * Does not trigger new location request.
     *
     * @param onResult Callback with location or null
     */
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(onResult: (Location?) -> Unit) {
        try {
            if (!hasLocationPermission()) {
                Log.w(TAG, "Cannot get last location: Permission denied")
                onResult(null)
                return
            }

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d(TAG, "Last known location: ${location.latitude}, ${location.longitude}")
                    } else {
                        Log.d(TAG, "No last known location available")
                    }
                    onResult(location)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting last location", e)
                    onResult(null)
                }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception getting last location", e)
            onResult(null)
        }
    }

    /**
     * Cleans up resources
     * Call when SensorManager is no longer needed
     */
    fun cleanup() {
        stopLocationUpdates()
        locationCallback = null
    }
}

/**
 * LocationResult - Sealed class representing location states
 *
 * Represents all possible states of location data:
 * - Idle: Not actively requesting location
 * - Success: Location data available
 * - Error: Error occurred
 * - PermissionDenied: User denied location permission
 */
sealed class LocationResult {
    /** No location updates active */
    object Idle : LocationResult()

    /** Location data received successfully */
    data class Success(val location: Location) : LocationResult()

    /** Error occurred while getting location */
    data class Error(val message: String) : LocationResult()

    /** Location permission not granted */
    object PermissionDenied : LocationResult()
}
