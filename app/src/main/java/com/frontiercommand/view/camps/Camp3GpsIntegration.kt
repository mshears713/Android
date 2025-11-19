package com.frontiercommand.view.camps

import android.Manifest
import android.app.Application
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.frontiercommand.repository.LocationResult
import com.frontiercommand.repository.SensorManager
import com.frontiercommand.ui.theme.PioneerTheme
import com.frontiercommand.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Camp3GpsIntegration - Educational content for GPS and location services
 *
 * This camp teaches accessing device sensors, specifically GPS, with proper
 * permission handling and real-time data display.
 *
 * **Learning Objectives:**
 * - Understand Android runtime permissions
 * - Access GPS/location services
 * - Display real-time location data
 * - Handle permission denied scenarios
 * - Learn location accuracy concepts
 *
 * **Interactive Features:**
 * - Permission request flow
 * - Live latitude/longitude display
 * - Accuracy meter
 * - Refresh button for manual updates
 * - GPS status indicators
 *
 * @see SensorManager for GPS implementation
 */
@Composable
fun Camp3GpsIntegration(
    modifier: Modifier = Modifier,
    viewModel: Camp3ViewModel = viewModel()
) {
    val context = LocalContext.current
    val locationResult by viewModel.locationResult.collectAsState()
    val hasPermission by viewModel.hasPermission.collectAsState()

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        viewModel.onPermissionResult(granted)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Camp 3: GPS Integration",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Introduction
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📍 What is GPS?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """
                        GPS (Global Positioning System) uses satellites to determine your device's location.
                        On Android, apps need explicit permission to access location data.

                        Key Concepts:
                        • Runtime Permissions: User must grant access
                        • Latitude/Longitude: Geographic coordinates
                        • Accuracy: Measurement precision (meters)
                        • FusedLocationProvider: Battery-efficient API

                        Privacy Note:
                        Location is sensitive data. Always request permission
                        and explain why your app needs it.
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Permission Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (hasPermission) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                }
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Location Permission",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (hasPermission) "✅ Granted" else "❌ Not Granted",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!hasPermission) {
                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        ) {
                            Text("Request Permission")
                        }
                    }
                }
            }
        }

        // Location Display
        if (hasPermission) {
            Text(
                text = "Current Location",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            when (val result = locationResult) {
                is LocationResult.Success -> {
                    val location = result.location
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )

                                IconButton(
                                    onClick = { viewModel.refreshLocation() }
                                ) {
                                    Icon(Icons.Default.Refresh, "Refresh")
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            LocationDataRow("Latitude", String.format("%.6f°", location.latitude))
                            LocationDataRow("Longitude", String.format("%.6f°", location.longitude))
                            LocationDataRow("Accuracy", String.format("%.1f meters", location.accuracy))
                            LocationDataRow("Altitude", String.format("%.1f meters", location.altitude))
                            LocationDataRow("Speed", String.format("%.1f m/s", location.speed))

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Last updated: ${java.text.SimpleDateFormat("HH:mm:ss").format(location.time)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                is LocationResult.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "⚠️ Error",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = result.message,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                LocationResult.Idle -> {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Acquiring GPS signal...")
                        }
                    }
                }
                LocationResult.PermissionDenied -> {
                    // Handled by permission card above
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (locationResult !is LocationResult.Success && locationResult !is LocationResult.Idle) {
                    Button(
                        onClick = { viewModel.startLocationUpdates() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Start Updates")
                    }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.stopLocationUpdates() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Stop Updates")
                    }
                }
            }
        }

        // Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🎓 Key Takeaways",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """
                        • Always request runtime permissions for location
                        • FusedLocationProvider is battery-efficient
                        • Latitude/Longitude define geographic position
                        • Accuracy varies (GPS, WiFi, Cell towers)
                        • Stop updates when not needed to save battery

                        Use location responsibly and respect user privacy!
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun LocationDataRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}

/**
 * Camp3ViewModel - Manages state for Camp 3
 */
class Camp3ViewModel(application: Application) : BaseViewModel(application) {

    private val sensorManager = SensorManager(application)

    private val _locationResult = MutableStateFlow<LocationResult>(LocationResult.Idle)
    val locationResult: StateFlow<LocationResult> = _locationResult.asStateFlow()

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    init {
        checkPermission()

        // Observe location updates
        launchSafe {
            sensorManager.locationFlow.collect { result ->
                _locationResult.value = result
            }
        }
    }

    private fun checkPermission() {
        _hasPermission.value = sensorManager.hasLocationPermission()
        if (_hasPermission.value) {
            startLocationUpdates()
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _hasPermission.value = granted
        if (granted) {
            startLocationUpdates()
        }
    }

    fun startLocationUpdates() {
        logInfo("Starting location updates")
        sensorManager.startLocationUpdates()
    }

    fun stopLocationUpdates() {
        logInfo("Stopping location updates")
        sensorManager.stopLocationUpdates()
    }

    fun refreshLocation() {
        logInfo("Refreshing location")
        sensorManager.getLastKnownLocation { location ->
            if (location != null) {
                _locationResult.value = LocationResult.Success(location)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.cleanup()
    }
}

@Preview(showBackground = true)
@Composable
fun Camp3GpsIntegrationPreview() {
    PioneerTheme {
        Surface {
            Camp3GpsIntegration()
        }
    }
}
