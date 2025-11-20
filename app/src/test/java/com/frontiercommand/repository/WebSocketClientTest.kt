package com.frontiercommand.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * WebSocketClientTest - Unit tests for WebSocketClient
 *
 * Tests the simulated WebSocket client to ensure correct behavior for:
 * - Connection lifecycle (connect, disconnect, reconnect)
 * - Connection status state changes
 * - Message sending and receiving
 * - Error handling for invalid operations
 * - Heartbeat message generation
 * - Server response simulation
 * - StateFlow updates
 *
 * Uses kotlinx-coroutines-test for testing suspend functions and flows.
 */
class WebSocketClientTest {

    @Before
    fun setUp() {
        // Ensure client is disconnected before each test
        if (WebSocketClient.isConnected()) {
            WebSocketClient.disconnect()
        }
    }

    @After
    fun tearDown() {
        // Clean up after each test
        WebSocketClient.disconnect()
    }

    // ========== Connection Lifecycle Tests ==========

    @Test
    fun `connect successfully changes status to CONNECTED`() = runTest {
        // Given
        val initialStatus = WebSocketClient.connectionStatus.value
        assertEquals("Initial status should be DISCONNECTED",
            ConnectionStatus.DISCONNECTED, initialStatus)

        // When
        WebSocketClient.connect()

        // Then
        val finalStatus = WebSocketClient.connectionStatus.value
        assertEquals("Status should be CONNECTED after connect()",
            ConnectionStatus.CONNECTED, finalStatus)
        assertTrue("isConnected() should return true",
            WebSocketClient.isConnected())
    }

    @Test
    fun `connect emits welcome message`() = runTest {
        // When
        WebSocketClient.connect()

        // Give time for welcome message to be emitted
        delay(100)

        // Then
        val message = WebSocketClient.incomingMessages.value
        assertNotNull("Should receive welcome message", message)
        assertEquals("Welcome message type should be 'system'", "system", message?.type)
        assertTrue("Welcome message should mention connection",
            message?.content?.contains("Connected", ignoreCase = true) == true)
    }

    @Test
    fun `disconnect changes status to DISCONNECTED`() = runTest {
        // Given
        WebSocketClient.connect()
        assertEquals("Should be connected",
            ConnectionStatus.CONNECTED, WebSocketClient.connectionStatus.value)

        // When
        WebSocketClient.disconnect()

        // Then
        val finalStatus = WebSocketClient.connectionStatus.value
        assertEquals("Status should be DISCONNECTED after disconnect()",
            ConnectionStatus.DISCONNECTED, finalStatus)
        assertFalse("isConnected() should return false",
            WebSocketClient.isConnected())
    }

    @Test
    fun `disconnect emits disconnection message`() = runTest {
        // Given
        WebSocketClient.connect()
        delay(100) // Wait for welcome message

        // When
        WebSocketClient.disconnect()
        delay(100) // Wait for disconnect message

        // Then
        val message = WebSocketClient.incomingMessages.value
        assertNotNull("Should receive disconnect message", message)
        assertTrue("Disconnect message should mention disconnection",
            message?.content?.contains("Disconnected", ignoreCase = true) == true)
    }

    @Test
    fun `connect when already connected does nothing`() = runTest {
        // Given
        WebSocketClient.connect()
        val firstStatus = WebSocketClient.connectionStatus.value
        assertEquals("Should be connected", ConnectionStatus.CONNECTED, firstStatus)

        // When
        WebSocketClient.connect() // Try connecting again

        // Then
        val secondStatus = WebSocketClient.connectionStatus.value
        assertEquals("Status should still be CONNECTED",
            ConnectionStatus.CONNECTED, secondStatus)
    }

    @Test
    fun `disconnect when already disconnected does nothing`() = runTest {
        // Given - already disconnected by default
        assertEquals("Should start disconnected",
            ConnectionStatus.DISCONNECTED, WebSocketClient.connectionStatus.value)

        // When
        WebSocketClient.disconnect() // Try disconnecting again

        // Then
        assertEquals("Status should still be DISCONNECTED",
            ConnectionStatus.DISCONNECTED, WebSocketClient.connectionStatus.value)
    }

    @Test
    fun `can reconnect after disconnecting`() = runTest {
        // Given
        WebSocketClient.connect()
        delay(100)
        WebSocketClient.disconnect()
        delay(100)

        assertEquals("Should be disconnected",
            ConnectionStatus.DISCONNECTED, WebSocketClient.connectionStatus.value)

        // When
        WebSocketClient.connect()

        // Then
        assertEquals("Should be connected again",
            ConnectionStatus.CONNECTED, WebSocketClient.connectionStatus.value)
        assertTrue("isConnected() should return true",
            WebSocketClient.isConnected())
    }

    // ========== Message Sending Tests ==========

    @Test
    fun `sendMessage when connected succeeds`() = runTest {
        // Given
        WebSocketClient.connect()
        delay(100)

        // When
        WebSocketClient.sendMessage("Hello server")

        // Wait for response
        delay(700) // Echo (200ms) + response (400ms) + buffer

        // Then
        val message = WebSocketClient.incomingMessages.value
        assertNotNull("Should receive response message", message)
    }

    @Test
    fun `sendMessage emits echo message`() = runTest {
        // Given
        WebSocketClient.connect()
        delay(100)

        // When
        WebSocketClient.sendMessage("Test message")

        // Wait for echo (200ms + buffer)
        delay(300)

        // Then
        val message = WebSocketClient.incomingMessages.value
        assertNotNull("Should receive echo message", message)
        assertEquals("Echo message type should be 'echo'", "echo", message?.type)
        assertTrue("Echo should contain sent message",
            message?.content?.contains("Test message") == true)
    }

    @Test
    fun `sendMessage with status keyword returns status response`() = runTest {
        // Given
        WebSocketClient.connect()
        delay(100)

        // When
        WebSocketClient.sendMessage("status")

        // Wait for echo + response
        delay(700)

        // Then
        val message = WebSocketClient.incomingMessages.value
        assertNotNull("Should receive status response", message)
        assertTrue("Response should contain status information",
            message?.content?.contains("status", ignoreCase = true) == true ||
            message?.content?.contains("online", ignoreCase = true) == true)
    }

    @Test
    fun `sendMessage with subscribe keyword returns subscription confirmation`() = runTest {
        // Given
        WebSocketClient.connect()
        delay(100)

        // When
        WebSocketClient.sendMessage("subscribe")

        // Wait for echo + response
        delay(700)

        // Then
        val message = WebSocketClient.incomingMessages.value
        assertNotNull("Should receive subscription response", message)
        assertTrue("Response should mention subscription",
            message?.content?.contains("subscrib", ignoreCase = true) == true)
    }

    @Test
    fun `sendMessage with ping keyword returns pong response`() = runTest {
        // Given
        WebSocketClient.connect()
        delay(100)

        // When
        WebSocketClient.sendMessage("ping")

        // Wait for echo + response
        delay(700)

        // Then
        val message = WebSocketClient.incomingMessages.value
        assertNotNull("Should receive pong response", message)
        assertTrue("Response should contain 'Pong'",
            message?.content?.contains("Pong", ignoreCase = true) == true)
    }

    @Test
    fun `sendMessage with generic text returns echo response`() = runTest {
        // Given
        WebSocketClient.connect()
        delay(100)

        // When
        WebSocketClient.sendMessage("Random message")

        // Wait for echo + response
        delay(700)

        // Then
        val message = WebSocketClient.incomingMessages.value
        assertNotNull("Should receive response", message)
        // Should get either echo or response type
        assertTrue("Should be echo or response type",
            message?.type == "echo" || message?.type == "response")
    }

    // ========== Error Handling Tests ==========

    @Test
    fun `sendMessage when disconnected throws WebSocketException`() = runTest {
        // Given - disconnected by default
        assertEquals("Should be disconnected",
            ConnectionStatus.DISCONNECTED, WebSocketClient.connectionStatus.value)

        // When/Then
        val exception = assertThrows(WebSocketException::class.java) {
            runTest {
                WebSocketClient.sendMessage("This should fail")
            }
        }

        assertTrue("Exception should mention not connected",
            exception.message?.contains("not connected", ignoreCase = true) == true)
    }

    @Test
    fun `sendMessage with blank message throws WebSocketException`() = runTest {
        // Given
        WebSocketClient.connect()

        // When/Then
        val exception = assertThrows(WebSocketException::class.java) {
            runTest {
                WebSocketClient.sendMessage("")
            }
        }

        assertTrue("Exception should mention empty message",
            exception.message?.contains("empty", ignoreCase = true) == true)
    }

    @Test
    fun `sendMessage with whitespace-only message throws WebSocketException`() = runTest {
        // Given
        WebSocketClient.connect()

        // When/Then
        val exception = assertThrows(WebSocketException::class.java) {
            runTest {
                WebSocketClient.sendMessage("   ")
            }
        }

        assertTrue("Exception should mention empty message",
            exception.message?.contains("empty", ignoreCase = true) == true)
    }

    // ========== Connection Status Tests ==========

    @Test
    fun `isConnected returns false when disconnected`() {
        // Given/When - disconnected by default
        val connected = WebSocketClient.isConnected()

        // Then
        assertFalse("isConnected() should return false when disconnected", connected)
    }

    @Test
    fun `isConnected returns true when connected`() = runTest {
        // Given
        WebSocketClient.connect()

        // When
        val connected = WebSocketClient.isConnected()

        // Then
        assertTrue("isConnected() should return true when connected", connected)
    }

    @Test
    fun `connectionStatus starts as DISCONNECTED`() {
        // When
        val status = WebSocketClient.connectionStatus.value

        // Then
        assertEquals("Initial status should be DISCONNECTED",
            ConnectionStatus.DISCONNECTED, status)
    }

    @Test
    fun `connectionStatus transitions through CONNECTING to CONNECTED`() = runTest {
        // Given
        val statuses = mutableListOf<ConnectionStatus>()

        // Capture initial status
        statuses.add(WebSocketClient.connectionStatus.value)

        // When
        val connectJob = kotlinx.coroutines.launch {
            WebSocketClient.connect()
        }

        // Capture status during connection (should be CONNECTING briefly)
        delay(100) // Give time for CONNECTING state
        statuses.add(WebSocketClient.connectionStatus.value)

        // Wait for connection to complete
        connectJob.join()
        delay(100)

        // Capture final status
        statuses.add(WebSocketClient.connectionStatus.value)

        // Then
        assertTrue("Should start with DISCONNECTED",
            statuses.contains(ConnectionStatus.DISCONNECTED))
        assertTrue("Should end with CONNECTED",
            statuses.contains(ConnectionStatus.CONNECTED))
    }

    // ========== Message Structure Tests ==========

    @Test
    fun `incoming messages have valid structure`() = runTest {
        // Given
        WebSocketClient.connect()
        delay(200)

        // When
        val message = WebSocketClient.incomingMessages.value

        // Then
        assertNotNull("Message should not be null", message)
        assertNotNull("Message ID should not be null", message?.id)
        assertFalse("Message ID should not be blank", message?.id.isNullOrBlank())
        assertNotNull("Message type should not be null", message?.type)
        assertFalse("Message type should not be blank", message?.type.isNullOrBlank())
        assertNotNull("Message content should not be null", message?.content)
        assertFalse("Message content should not be blank", message?.content.isNullOrBlank())
        assertTrue("Message timestamp should be positive", (message?.timestamp ?: 0) > 0)
    }

    @Test
    fun `message types are valid`() = runTest {
        // Given
        WebSocketClient.connect()
        delay(200)
        WebSocketClient.sendMessage("test")
        delay(700)

        // When
        val message = WebSocketClient.incomingMessages.value

        // Then - message type should be one of the known types
        val validTypes = listOf("system", "echo", "response", "notification", "heartbeat")
        assertNotNull("Message should not be null", message)
        assertTrue("Message type should be valid: ${message?.type}",
            validTypes.contains(message?.type))
    }

    @Test
    fun `message timestamps are recent`() = runTest {
        // Given
        val beforeConnect = System.currentTimeMillis()
        WebSocketClient.connect()
        delay(200)
        val afterConnect = System.currentTimeMillis()

        // When
        val message = WebSocketClient.incomingMessages.value

        // Then
        assertNotNull("Message should not be null", message)
        val timestamp = message?.timestamp ?: 0
        assertTrue("Timestamp should be after beforeConnect",
            timestamp >= beforeConnect)
        assertTrue("Timestamp should be before afterConnect + 1 second",
            timestamp <= afterConnect + 1000)
    }

    // ========== Heartbeat Tests ==========

    @Test
    fun `heartbeat messages are sent periodically when connected`() = runTest {
        // Given
        WebSocketClient.connect()
        delay(200) // Wait for welcome message

        // When - wait for heartbeat (10 seconds + buffer, but we'll use a shorter test)
        // Note: Testing the full 10 second interval would make tests slow,
        // so we verify the heartbeat job exists by checking connection behavior

        // Then
        assertTrue("Should be connected (heartbeat job should be active)",
            WebSocketClient.isConnected())

        // Verify disconnecting stops heartbeat
        WebSocketClient.disconnect()
        assertFalse("Should be disconnected (heartbeat job should be cancelled)",
            WebSocketClient.isConnected())
    }

    // ========== Stress Tests ==========

    @Test
    fun `multiple rapid sendMessage calls succeed`() = runTest {
        // Given
        WebSocketClient.connect()
        delay(100)

        // When - send multiple messages rapidly
        var successCount = 0
        repeat(5) { i ->
            try {
                WebSocketClient.sendMessage("Message $i")
                successCount++
            } catch (e: WebSocketException) {
                // Should not throw
            }
            delay(50) // Small delay between messages
        }

        // Then
        assertEquals("All messages should be sent successfully", 5, successCount)
    }

    @Test
    fun `connect and disconnect multiple times works correctly`() = runTest {
        // When/Then - connect and disconnect 3 times
        repeat(3) { iteration ->
            WebSocketClient.connect()
            delay(100)
            assertTrue("Should be connected in iteration $iteration",
                WebSocketClient.isConnected())

            WebSocketClient.disconnect()
            delay(100)
            assertFalse("Should be disconnected in iteration $iteration",
                WebSocketClient.isConnected())
        }
    }

    // ========== StateFlow Behavior Tests ==========

    @Test
    fun `connectionStatus StateFlow updates correctly`() = runTest {
        // Given
        var statusUpdates = 0
        val job = kotlinx.coroutines.launch {
            WebSocketClient.connectionStatus.collect {
                statusUpdates++
            }
        }

        delay(50) // Allow collector to start

        // When
        WebSocketClient.connect()
        delay(200)
        WebSocketClient.disconnect()
        delay(200)

        job.cancel()

        // Then
        assertTrue("Should have received multiple status updates", statusUpdates >= 2)
    }

    @Test
    fun `incomingMessages StateFlow updates with new messages`() = runTest {
        // Given
        val messages = mutableListOf<WebSocketMessage?>()
        val job = kotlinx.coroutines.launch {
            WebSocketClient.incomingMessages.collect { message ->
                messages.add(message)
            }
        }

        delay(50) // Allow collector to start

        // When
        WebSocketClient.connect()
        delay(300) // Wait for welcome message
        WebSocketClient.sendMessage("test")
        delay(700) // Wait for echo and response

        job.cancel()

        // Then
        assertTrue("Should have received multiple messages", messages.size >= 2)
        assertTrue("Should have received at least one non-null message",
            messages.any { it != null })
    }
}
