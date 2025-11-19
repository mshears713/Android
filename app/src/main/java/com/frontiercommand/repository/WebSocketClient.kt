package com.frontiercommand.repository

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * WebSocketClient - Placeholder WebSocket client for real-time communication simulation
 *
 * This is a **simulated** WebSocket client that mimics bidirectional real-time
 * communication without requiring actual WebSocket libraries or servers. Designed
 * to teach WebSocket concepts with complete offline functionality.
 *
 * **Why a Placeholder?**
 * - No external dependencies on OkHttp WebSocket, Ktor, or Scarlet
 * - Works completely offline without backend services
 * - Simplifies learning by focusing on concepts, not configuration
 * - Predictable message flows for educational purposes
 *
 * **Features:**
 * - Simulates connection lifecycle (connecting, connected, disconnected)
 * - Bidirectional message flow with StateFlow
 * - Automatic heartbeat messages when connected
 * - Simulated server responses to client messages
 * - Thread-safe singleton implementation
 * - Connection status observable via StateFlow
 *
 * **Message Flow:**
 * ```
 * Client → sendMessage() → Simulated server processing → Incoming message → StateFlow
 * Server → Auto-generated events → Incoming message → StateFlow
 * ```
 *
 * **Usage Example:**
 * ```kotlin
 * viewModelScope.launch {
 *     // Collect connection status
 *     WebSocketClient.connectionStatus.collect { status ->
 *         Log.d("WS", "Status: $status")
 *     }
 * }
 *
 * // Connect
 * WebSocketClient.connect()
 *
 * // Send message
 * WebSocketClient.sendMessage("Hello server!")
 *
 * // Collect incoming messages
 * WebSocketClient.incomingMessages.collect { message ->
 *     Log.d("WS", "Received: $message")
 * }
 *
 * // Disconnect
 * WebSocketClient.disconnect()
 * ```
 *
 * @see ConnectionStatus for connection states
 */
object WebSocketClient {

    private const val TAG = "WebSocketClient"

    /**
     * JSON serializer for message formatting
     */
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /**
     * Coroutine scope for managing WebSocket operations
     * Uses SupervisorJob to prevent child failures from cancelling parent
     */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Job for the heartbeat coroutine (cancelled when disconnected)
     */
    private var heartbeatJob: Job? = null

    /**
     * Job for simulated incoming messages
     */
    private var messageGeneratorJob: Job? = null

    /**
     * Internal mutable connection status
     */
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)

    /**
     * Public read-only connection status
     * UI can collect this to display connection state
     */
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    /**
     * Internal mutable incoming messages flow
     */
    private val _incomingMessages = MutableStateFlow<WebSocketMessage?>(null)

    /**
     * Public read-only incoming messages
     * UI can collect this to receive messages from server
     */
    val incomingMessages: StateFlow<WebSocketMessage?> = _incomingMessages.asStateFlow()

    /**
     * Message counter for generating unique message IDs
     */
    private var messageCounter = 0

    /**
     * Connects to the simulated WebSocket server
     *
     * Simulates the connection handshake and starts heartbeat/message generation.
     * If already connected, this is a no-op.
     *
     * @throws WebSocketException if connection fails
     */
    suspend fun connect() {
        if (_connectionStatus.value == ConnectionStatus.CONNECTED) {
            Log.w(TAG, "Already connected")
            return
        }

        try {
            Log.d(TAG, "Connecting to WebSocket server...")
            _connectionStatus.value = ConnectionStatus.CONNECTING

            // Simulate connection delay
            delay(800)

            // Successfully connected
            _connectionStatus.value = ConnectionStatus.CONNECTED
            Log.i(TAG, "Connected successfully")

            // Send welcome message
            emitIncomingMessage(
                WebSocketMessage(
                    id = UUID.randomUUID().toString(),
                    type = "system",
                    content = "Connected to Raspberry Pi WebSocket server",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Start heartbeat
            startHeartbeat()

            // Start simulated incoming message generator
            startMessageGenerator()

        } catch (e: Exception) {
            Log.e(TAG, "Connection failed", e)
            _connectionStatus.value = ConnectionStatus.DISCONNECTED
            throw WebSocketException("Failed to connect: ${e.message}", e)
        }
    }

    /**
     * Disconnects from the simulated WebSocket server
     *
     * Stops heartbeat and message generation, cleans up resources.
     */
    fun disconnect() {
        if (_connectionStatus.value == ConnectionStatus.DISCONNECTED) {
            Log.w(TAG, "Already disconnected")
            return
        }

        try {
            Log.d(TAG, "Disconnecting from WebSocket server...")

            // Stop heartbeat and message generator
            heartbeatJob?.cancel()
            messageGeneratorJob?.cancel()

            // Update status
            _connectionStatus.value = ConnectionStatus.DISCONNECTED
            Log.i(TAG, "Disconnected successfully")

            // Send disconnection message
            scope.launch {
                emitIncomingMessage(
                    WebSocketMessage(
                        id = UUID.randomUUID().toString(),
                        type = "system",
                        content = "Disconnected from server",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error during disconnect", e)
        }
    }

    /**
     * Sends a message to the simulated WebSocket server
     *
     * Simulates client-to-server message sending with automatic server response.
     *
     * @param message The message content to send
     * @throws WebSocketException if not connected or send fails
     */
    suspend fun sendMessage(message: String) {
        if (_connectionStatus.value != ConnectionStatus.CONNECTED) {
            throw WebSocketException("Cannot send message: Not connected")
        }

        if (message.isBlank()) {
            throw WebSocketException("Cannot send empty message")
        }

        try {
            Log.d(TAG, "Sending message: $message")

            // Simulate network latency
            delay(200)

            // Echo the sent message
            emitIncomingMessage(
                WebSocketMessage(
                    id = UUID.randomUUID().toString(),
                    type = "echo",
                    content = "You sent: $message",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Simulate server processing and response
            delay(400)

            // Generate appropriate response based on message content
            val response = generateServerResponse(message)
            emitIncomingMessage(response)

            Log.d(TAG, "Message sent successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
            throw WebSocketException("Failed to send message: ${e.message}", e)
        }
    }

    /**
     * Starts the heartbeat coroutine
     * Sends periodic heartbeat messages to keep connection alive
     */
    private fun startHeartbeat() {
        heartbeatJob = scope.launch {
            while (isActive && _connectionStatus.value == ConnectionStatus.CONNECTED) {
                delay(10000) // Heartbeat every 10 seconds

                emitIncomingMessage(
                    WebSocketMessage(
                        id = UUID.randomUUID().toString(),
                        type = "heartbeat",
                        content = "❤️ Heartbeat",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    /**
     * Starts simulated incoming message generator
     * Generates periodic server-initiated messages
     */
    private fun startMessageGenerator() {
        messageGeneratorJob = scope.launch {
            delay(5000) // First message after 5 seconds

            val serverMessages = listOf(
                "Server: System temperature: 45°C",
                "Server: New device connected: pi-004",
                "Server: Configuration updated",
                "Server: Backup completed successfully",
                "Server: CPU usage: 23%",
                "Server: Memory usage: 512MB / 8GB"
            )

            var messageIndex = 0

            while (isActive && _connectionStatus.value == ConnectionStatus.CONNECTED) {
                delay((15000..30000).random().toLong()) // Random delay between messages

                emitIncomingMessage(
                    WebSocketMessage(
                        id = UUID.randomUUID().toString(),
                        type = "notification",
                        content = serverMessages[messageIndex % serverMessages.size],
                        timestamp = System.currentTimeMillis()
                    )
                )

                messageIndex++
            }
        }
    }

    /**
     * Generates a simulated server response based on client message
     *
     * @param clientMessage The message sent by client
     * @return Server response message
     */
    private fun generateServerResponse(clientMessage: String): WebSocketMessage {
        val content = when {
            clientMessage.contains("status", ignoreCase = true) -> {
                json.encodeToString(
                    mapOf(
                        "status" to "online",
                        "uptime" to "3d 14h 23m",
                        "active_connections" to 5
                    )
                )
            }
            clientMessage.contains("subscribe", ignoreCase = true) -> {
                "Subscribed to events. You'll receive real-time updates."
            }
            clientMessage.contains("unsubscribe", ignoreCase = true) -> {
                "Unsubscribed from events."
            }
            clientMessage.contains("ping", ignoreCase = true) -> {
                "Pong! Latency: ${(50..150).random()}ms"
            }
            else -> {
                "Server received: \"$clientMessage\""
            }
        }

        return WebSocketMessage(
            id = UUID.randomUUID().toString(),
            type = "response",
            content = content,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Emits an incoming message to the StateFlow
     *
     * @param message The message to emit
     */
    private suspend fun emitIncomingMessage(message: WebSocketMessage) {
        _incomingMessages.value = message
        Log.d(TAG, "Incoming message: ${message.content}")
    }

    /**
     * Checks if currently connected
     *
     * @return true if connected, false otherwise
     */
    fun isConnected(): Boolean {
        return _connectionStatus.value == ConnectionStatus.CONNECTED
    }

    /**
     * Cleans up resources
     * Call this when the app is closing
     */
    fun cleanup() {
        disconnect()
        scope.cancel()
    }
}

/**
 * ConnectionStatus - WebSocket connection state enumeration
 */
enum class ConnectionStatus {
    /** Not connected to server */
    DISCONNECTED,

    /** Currently establishing connection */
    CONNECTING,

    /** Successfully connected and ready to send/receive */
    CONNECTED,

    /** Connection error occurred */
    ERROR
}

/**
 * WebSocketMessage - Represents a message sent or received via WebSocket
 *
 * @property id Unique message identifier
 * @property type Message type (system, echo, response, notification, heartbeat)
 * @property content Message content
 * @property timestamp When the message was created (milliseconds since epoch)
 */
@Serializable
data class WebSocketMessage(
    val id: String,
    val type: String,
    val content: String,
    val timestamp: Long
)

/**
 * WebSocketException - Custom exception for WebSocket errors
 *
 * @param message Human-readable error description
 * @param cause The underlying exception (if any)
 */
class WebSocketException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
