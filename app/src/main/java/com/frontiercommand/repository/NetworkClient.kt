package com.frontiercommand.repository

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * NetworkClient - Placeholder HTTP client for REST API simulation
 *
 * This is a **simulated** HTTP client that mimics real network behavior without
 * requiring actual network libraries or external services. It's designed to teach
 * HTTP networking concepts while maintaining complete offline functionality.
 *
 * **Why a Placeholder?**
 * - No external dependencies on Retrofit, Ktor, or OkHttp
 * - Works completely offline without backend services
 * - Simplifies learning by focusing on concepts, not configuration
 * - Predictable responses for educational purposes
 *
 * **Features:**
 * - Simulates network latency with realistic delays
 * - Returns hardcoded JSON responses for known endpoints
 * - Comprehensive error handling and logging
 * - Thread-safe singleton implementation
 * - Supports GET and POST operations
 *
 * **Simulated Endpoints:**
 * ```
 * GET  /status          → System status JSON
 * GET  /devices         → List of devices
 * GET  /logs            → Recent log entries
 * POST /config          → Configuration update response
 * POST /command         → Command execution response
 * ```
 *
 * **Usage Example:**
 * ```kotlin
 * viewModelScope.launch {
 *     try {
 *         val response = NetworkClient.get("/status")
 *         Log.d("Network", "Response: $response")
 *     } catch (e: NetworkException) {
 *         Log.e("Network", "Error: ${e.message}")
 *     }
 * }
 * ```
 *
 * @see NetworkException for error types
 */
object NetworkClient {

    private const val TAG = "NetworkClient"

    /**
     * Base simulated network delay in milliseconds
     * Randomized slightly to feel more realistic
     */
    private const val BASE_DELAY_MS = 500L
    private const val DELAY_VARIANCE_MS = 300L

    /**
     * JSON serializer with pretty printing for readable responses
     */
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /**
     * Simulates network latency with random variance
     * Adds realism to placeholder network calls
     */
    private suspend fun simulateNetworkDelay() {
        val delay = BASE_DELAY_MS + (0..DELAY_VARIANCE_MS).random()
        delay(delay)
    }

    /**
     * Performs a simulated HTTP GET request
     *
     * Simulates fetching data from a REST API endpoint. Returns hardcoded
     * JSON responses based on the URL path.
     *
     * **Supported URLs:**
     * - `/status` - System status with uptime and memory
     * - `/devices` - List of connected devices
     * - `/logs` - Recent log entries
     * - `/config` - Current configuration
     *
     * @param url The endpoint URL (e.g., "/status")
     * @return JSON string response
     * @throws NetworkException if the request fails
     */
    suspend fun get(url: String): String {
        return try {
            Log.d(TAG, "GET request to: $url")

            // Validate URL
            if (url.isBlank()) {
                throw NetworkException("URL cannot be blank")
            }

            // Simulate network latency
            simulateNetworkDelay()

            // Return hardcoded response based on URL
            val response = when {
                url.contains("/status") -> {
                    json.encodeToString(
                        StatusResponse(
                            status = "online",
                            uptime = System.currentTimeMillis() / 1000,
                            memory = MemoryInfo(
                                total = 8192,
                                used = 4096,
                                free = 4096
                            ),
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                url.contains("/devices") -> {
                    json.encodeToString(
                        DevicesResponse(
                            devices = listOf(
                                Device("pi-001", "Raspberry Pi 4", "online"),
                                Device("pi-002", "Raspberry Pi Zero", "offline"),
                                Device("pi-003", "Raspberry Pi 3", "online")
                            )
                        )
                    )
                }
                url.contains("/logs") -> {
                    json.encodeToString(
                        LogsResponse(
                            logs = listOf(
                                "2024-01-01 12:00:00 - System started",
                                "2024-01-01 12:05:23 - Device pi-001 connected",
                                "2024-01-01 12:10:45 - Configuration updated"
                            )
                        )
                    )
                }
                url.contains("/config") -> {
                    json.encodeToString(
                        ConfigResponse(
                            config = mapOf(
                                "wifi_enabled" to "true",
                                "auto_update" to "false",
                                "log_level" to "info"
                            )
                        )
                    )
                }
                else -> {
                    throw NetworkException("Unknown endpoint: $url")
                }
            }

            Log.d(TAG, "GET response from $url: ${response.take(100)}...")
            response

        } catch (e: NetworkException) {
            Log.e(TAG, "Network error during GET $url", e)
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during GET $url", e)
            throw NetworkException("GET request failed: ${e.message}", e)
        }
    }

    /**
     * Performs a simulated HTTP POST request
     *
     * Simulates sending data to a REST API endpoint. Returns hardcoded
     * success or error responses based on the URL and body.
     *
     * **Supported URLs:**
     * - `/config` - Update configuration
     * - `/command` - Execute command
     * - `/upload` - Upload data
     *
     * @param url The endpoint URL (e.g., "/command")
     * @param body The request body as JSON string
     * @return JSON string response
     * @throws NetworkException if the request fails
     */
    suspend fun post(url: String, body: String): String {
        return try {
            Log.d(TAG, "POST request to: $url with body: ${body.take(100)}...")

            // Validate inputs
            if (url.isBlank()) {
                throw NetworkException("URL cannot be blank")
            }
            if (body.isBlank()) {
                throw NetworkException("Request body cannot be blank")
            }

            // Simulate network latency
            simulateNetworkDelay()

            // Return hardcoded response based on URL
            val response = when {
                url.contains("/config") -> {
                    json.encodeToString(
                        GenericResponse(
                            success = true,
                            message = "Configuration updated successfully",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                url.contains("/command") -> {
                    json.encodeToString(
                        CommandResponse(
                            success = true,
                            message = "Command executed successfully",
                            output = "Command completed with exit code 0",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                url.contains("/upload") -> {
                    json.encodeToString(
                        GenericResponse(
                            success = true,
                            message = "Data uploaded successfully",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                else -> {
                    throw NetworkException("Unknown endpoint: $url")
                }
            }

            Log.d(TAG, "POST response from $url: ${response.take(100)}...")
            response

        } catch (e: NetworkException) {
            Log.e(TAG, "Network error during POST $url", e)
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during POST $url", e)
            throw NetworkException("POST request failed: ${e.message}", e)
        }
    }

    /**
     * Checks if a URL is a valid endpoint
     * Used for validation before making requests
     *
     * @param url The URL to validate
     * @return true if the endpoint is recognized
     */
    fun isValidEndpoint(url: String): Boolean {
        return url.contains("/status") ||
                url.contains("/devices") ||
                url.contains("/logs") ||
                url.contains("/config") ||
                url.contains("/command") ||
                url.contains("/upload")
    }
}

/**
 * NetworkException - Custom exception for network errors
 *
 * Thrown when simulated network operations fail. Wraps the underlying
 * cause and provides context-specific error messages.
 *
 * @param message Human-readable error description
 * @param cause The underlying exception (if any)
 */
class NetworkException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

// Response data classes for JSON serialization

@Serializable
data class StatusResponse(
    val status: String,
    val uptime: Long,
    val memory: MemoryInfo,
    val timestamp: Long
)

@Serializable
data class MemoryInfo(
    val total: Int,
    val used: Int,
    val free: Int
)

@Serializable
data class DevicesResponse(
    val devices: List<Device>
)

@Serializable
data class Device(
    val id: String,
    val name: String,
    val status: String
)

@Serializable
data class LogsResponse(
    val logs: List<String>
)

@Serializable
data class ConfigResponse(
    val config: Map<String, String>
)

@Serializable
data class GenericResponse(
    val success: Boolean,
    val message: String,
    val timestamp: Long
)

@Serializable
data class CommandResponse(
    val success: Boolean,
    val message: String,
    val output: String,
    val timestamp: Long
)
