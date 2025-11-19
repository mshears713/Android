package com.frontiercommand.repository

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

/**
 * NetworkClientTest - Unit tests for NetworkClient
 *
 * Tests the simulated REST API client to ensure correct behavior for:
 * - GET requests to all supported endpoints
 * - POST requests with request bodies
 * - Error handling for invalid inputs
 * - Endpoint validation
 * - JSON response parsing
 * - Network delay simulation
 *
 * Uses kotlinx-coroutines-test for testing suspend functions.
 */
class NetworkClientTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    // ========== GET Request Tests ==========

    @Test
    fun `GET status endpoint returns valid status response`() = runTest {
        // When
        val response = NetworkClient.get("/status")

        // Then
        assertNotNull("Response should not be null", response)
        assertTrue("Response should contain status field", response.contains("\"status\""))
        assertTrue("Response should contain uptime field", response.contains("\"uptime\""))
        assertTrue("Response should contain memory field", response.contains("\"memory\""))
        assertTrue("Response should contain timestamp field", response.contains("\"timestamp\""))

        // Verify it can be parsed as StatusResponse
        val statusResponse = json.decodeFromString<StatusResponse>(response)
        assertEquals("Status should be online", "online", statusResponse.status)
        assertNotNull("Memory info should not be null", statusResponse.memory)
        assertTrue("Uptime should be positive", statusResponse.uptime > 0)
    }

    @Test
    fun `GET devices endpoint returns valid devices response`() = runTest {
        // When
        val response = NetworkClient.get("/devices")

        // Then
        assertNotNull("Response should not be null", response)
        assertTrue("Response should contain devices field", response.contains("\"devices\""))

        // Verify it can be parsed as DevicesResponse
        val devicesResponse = json.decodeFromString<DevicesResponse>(response)
        assertNotNull("Devices list should not be null", devicesResponse.devices)
        assertTrue("Devices list should not be empty", devicesResponse.devices.isNotEmpty())
        assertEquals("Should have 3 devices", 3, devicesResponse.devices.size)

        // Verify device structure
        val firstDevice = devicesResponse.devices[0]
        assertNotNull("Device ID should not be null", firstDevice.id)
        assertNotNull("Device name should not be null", firstDevice.name)
        assertNotNull("Device status should not be null", firstDevice.status)
    }

    @Test
    fun `GET logs endpoint returns valid logs response`() = runTest {
        // When
        val response = NetworkClient.get("/logs")

        // Then
        assertNotNull("Response should not be null", response)
        assertTrue("Response should contain logs field", response.contains("\"logs\""))

        // Verify it can be parsed as LogsResponse
        val logsResponse = json.decodeFromString<LogsResponse>(response)
        assertNotNull("Logs list should not be null", logsResponse.logs)
        assertTrue("Logs list should not be empty", logsResponse.logs.isNotEmpty())
        assertTrue("Logs should contain system started message",
            logsResponse.logs.any { it.contains("System started") })
    }

    @Test
    fun `GET config endpoint returns valid config response`() = runTest {
        // When
        val response = NetworkClient.get("/config")

        // Then
        assertNotNull("Response should not be null", response)
        assertTrue("Response should contain config field", response.contains("\"config\""))

        // Verify it can be parsed as ConfigResponse
        val configResponse = json.decodeFromString<ConfigResponse>(response)
        assertNotNull("Config map should not be null", configResponse.config)
        assertTrue("Config should not be empty", configResponse.config.isNotEmpty())
        assertTrue("Config should contain wifi_enabled", configResponse.config.containsKey("wifi_enabled"))
        assertTrue("Config should contain auto_update", configResponse.config.containsKey("auto_update"))
        assertTrue("Config should contain log_level", configResponse.config.containsKey("log_level"))
    }

    @Test
    fun `GET with URL containing status returns status response`() = runTest {
        // When - URLs with /status anywhere should work
        val response1 = NetworkClient.get("https://example.com/api/status")
        val response2 = NetworkClient.get("/v1/status")

        // Then
        assertTrue("Response should contain status field", response1.contains("\"status\""))
        assertTrue("Response should contain status field", response2.contains("\"status\""))
    }

    // ========== POST Request Tests ==========

    @Test
    fun `POST config endpoint returns success response`() = runTest {
        // Given
        val requestBody = """{"wifi_enabled": "true", "log_level": "debug"}"""

        // When
        val response = NetworkClient.post("/config", requestBody)

        // Then
        assertNotNull("Response should not be null", response)
        assertTrue("Response should contain success field", response.contains("\"success\""))
        assertTrue("Response should contain message field", response.contains("\"message\""))

        // Verify it can be parsed as GenericResponse
        val genericResponse = json.decodeFromString<GenericResponse>(response)
        assertTrue("Success should be true", genericResponse.success)
        assertTrue("Message should contain 'updated'",
            genericResponse.message.contains("updated", ignoreCase = true))
        assertTrue("Timestamp should be positive", genericResponse.timestamp > 0)
    }

    @Test
    fun `POST command endpoint returns command response`() = runTest {
        // Given
        val requestBody = """{"command": "restart"}"""

        // When
        val response = NetworkClient.post("/command", requestBody)

        // Then
        assertNotNull("Response should not be null", response)
        assertTrue("Response should contain success field", response.contains("\"success\""))
        assertTrue("Response should contain output field", response.contains("\"output\""))

        // Verify it can be parsed as CommandResponse
        val commandResponse = json.decodeFromString<CommandResponse>(response)
        assertTrue("Success should be true", commandResponse.success)
        assertTrue("Message should contain 'executed'",
            commandResponse.message.contains("executed", ignoreCase = true))
        assertNotNull("Output should not be null", commandResponse.output)
        assertTrue("Output should contain 'exit code'",
            commandResponse.output.contains("exit code"))
    }

    @Test
    fun `POST upload endpoint returns success response`() = runTest {
        // Given
        val requestBody = """{"data": "sample data"}"""

        // When
        val response = NetworkClient.post("/upload", requestBody)

        // Then
        assertNotNull("Response should not be null", response)
        assertTrue("Response should contain success field", response.contains("\"success\""))

        // Verify it can be parsed as GenericResponse
        val genericResponse = json.decodeFromString<GenericResponse>(response)
        assertTrue("Success should be true", genericResponse.success)
        assertTrue("Message should contain 'uploaded'",
            genericResponse.message.contains("uploaded", ignoreCase = true))
    }

    // ========== Error Handling Tests ==========

    @Test
    fun `GET with blank URL throws NetworkException`() = runTest {
        // When/Then
        val exception = assertThrows(NetworkException::class.java) {
            runTest {
                NetworkClient.get("")
            }
        }

        assertTrue("Exception message should mention blank URL",
            exception.message?.contains("blank", ignoreCase = true) == true)
    }

    @Test
    fun `GET with unknown endpoint throws NetworkException`() = runTest {
        // When/Then
        val exception = assertThrows(NetworkException::class.java) {
            runTest {
                NetworkClient.get("/unknown-endpoint")
            }
        }

        assertTrue("Exception message should mention unknown endpoint",
            exception.message?.contains("unknown", ignoreCase = true) == true)
    }

    @Test
    fun `POST with blank URL throws NetworkException`() = runTest {
        // When/Then
        val exception = assertThrows(NetworkException::class.java) {
            runTest {
                NetworkClient.post("", """{"data": "test"}""")
            }
        }

        assertTrue("Exception message should mention blank URL",
            exception.message?.contains("blank", ignoreCase = true) == true)
    }

    @Test
    fun `POST with blank body throws NetworkException`() = runTest {
        // When/Then
        val exception = assertThrows(NetworkException::class.java) {
            runTest {
                NetworkClient.post("/config", "")
            }
        }

        assertTrue("Exception message should mention blank body",
            exception.message?.contains("blank", ignoreCase = true) == true)
    }

    @Test
    fun `POST with unknown endpoint throws NetworkException`() = runTest {
        // When/Then
        val exception = assertThrows(NetworkException::class.java) {
            runTest {
                NetworkClient.post("/unknown-endpoint", """{"data": "test"}""")
            }
        }

        assertTrue("Exception message should mention unknown endpoint",
            exception.message?.contains("unknown", ignoreCase = true) == true)
    }

    // ========== Endpoint Validation Tests ==========

    @Test
    fun `isValidEndpoint returns true for status endpoint`() {
        // When/Then
        assertTrue(NetworkClient.isValidEndpoint("/status"))
        assertTrue(NetworkClient.isValidEndpoint("https://api.example.com/status"))
    }

    @Test
    fun `isValidEndpoint returns true for devices endpoint`() {
        // When/Then
        assertTrue(NetworkClient.isValidEndpoint("/devices"))
        assertTrue(NetworkClient.isValidEndpoint("/api/devices"))
    }

    @Test
    fun `isValidEndpoint returns true for logs endpoint`() {
        // When/Then
        assertTrue(NetworkClient.isValidEndpoint("/logs"))
    }

    @Test
    fun `isValidEndpoint returns true for config endpoint`() {
        // When/Then
        assertTrue(NetworkClient.isValidEndpoint("/config"))
    }

    @Test
    fun `isValidEndpoint returns true for command endpoint`() {
        // When/Then
        assertTrue(NetworkClient.isValidEndpoint("/command"))
    }

    @Test
    fun `isValidEndpoint returns true for upload endpoint`() {
        // When/Then
        assertTrue(NetworkClient.isValidEndpoint("/upload"))
    }

    @Test
    fun `isValidEndpoint returns false for unknown endpoint`() {
        // When/Then
        assertFalse(NetworkClient.isValidEndpoint("/unknown"))
        assertFalse(NetworkClient.isValidEndpoint("/invalid"))
        assertFalse(NetworkClient.isValidEndpoint(""))
    }

    // ========== Response Structure Tests ==========

    @Test
    fun `StatusResponse contains all required fields`() = runTest {
        // When
        val response = NetworkClient.get("/status")
        val statusResponse = json.decodeFromString<StatusResponse>(response)

        // Then
        assertNotNull("Status should not be null", statusResponse.status)
        assertTrue("Uptime should be positive", statusResponse.uptime >= 0)
        assertNotNull("Memory should not be null", statusResponse.memory)
        assertTrue("Timestamp should be positive", statusResponse.timestamp > 0)

        // Memory validation
        assertEquals("Total memory should be 8192", 8192, statusResponse.memory.total)
        assertEquals("Used memory should be 4096", 4096, statusResponse.memory.used)
        assertEquals("Free memory should be 4096", 4096, statusResponse.memory.free)
    }

    @Test
    fun `DevicesResponse contains valid device structure`() = runTest {
        // When
        val response = NetworkClient.get("/devices")
        val devicesResponse = json.decodeFromString<DevicesResponse>(response)

        // Then
        assertTrue("Should have at least one device", devicesResponse.devices.isNotEmpty())

        devicesResponse.devices.forEach { device ->
            assertNotNull("Device ID should not be null", device.id)
            assertFalse("Device ID should not be blank", device.id.isBlank())
            assertNotNull("Device name should not be null", device.name)
            assertFalse("Device name should not be blank", device.name.isBlank())
            assertNotNull("Device status should not be null", device.status)
            assertTrue("Device status should be 'online' or 'offline'",
                device.status == "online" || device.status == "offline")
        }
    }

    @Test
    fun `POST responses contain timestamp`() = runTest {
        // Given
        val requestBody = """{"test": "data"}"""

        // When
        val configResponse = NetworkClient.post("/config", requestBody)
        val commandResponse = NetworkClient.post("/command", requestBody)
        val uploadResponse = NetworkClient.post("/upload", requestBody)

        // Then
        assertTrue("Config response should contain timestamp",
            configResponse.contains("\"timestamp\""))
        assertTrue("Command response should contain timestamp",
            commandResponse.contains("\"timestamp\""))
        assertTrue("Upload response should contain timestamp",
            uploadResponse.contains("\"timestamp\""))

        // Verify timestamps are recent (within last 10 seconds)
        val now = System.currentTimeMillis()
        val configTimestamp = json.decodeFromString<GenericResponse>(configResponse).timestamp
        val commandTimestamp = json.decodeFromString<CommandResponse>(commandResponse).timestamp
        val uploadTimestamp = json.decodeFromString<GenericResponse>(uploadResponse).timestamp

        assertTrue("Config timestamp should be recent", now - configTimestamp < 10000)
        assertTrue("Command timestamp should be recent", now - commandTimestamp < 10000)
        assertTrue("Upload timestamp should be recent", now - uploadTimestamp < 10000)
    }

    // ========== Network Delay Tests ==========

    @Test
    fun `GET request includes simulated network delay`() = runTest {
        // Given
        val startTime = System.currentTimeMillis()

        // When
        NetworkClient.get("/status")

        // Then
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Network delay should be at least BASE_DELAY_MS (500ms)
        assertTrue("Request should take at least 500ms due to simulated delay",
            duration >= 500)

        // Should complete within reasonable time (base + variance + overhead = ~1 second)
        assertTrue("Request should complete within 2 seconds", duration < 2000)
    }

    @Test
    fun `POST request includes simulated network delay`() = runTest {
        // Given
        val startTime = System.currentTimeMillis()
        val requestBody = """{"test": "data"}"""

        // When
        NetworkClient.post("/config", requestBody)

        // Then
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Network delay should be at least BASE_DELAY_MS (500ms)
        assertTrue("Request should take at least 500ms due to simulated delay",
            duration >= 500)

        // Should complete within reasonable time
        assertTrue("Request should complete within 2 seconds", duration < 2000)
    }
}
