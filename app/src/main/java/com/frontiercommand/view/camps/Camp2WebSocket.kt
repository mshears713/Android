package com.frontiercommand.view.camps

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.frontiercommand.repository.ConnectionStatus
import com.frontiercommand.repository.WebSocketClient
import com.frontiercommand.repository.WebSocketMessage
import com.frontiercommand.ui.theme.PioneerTheme
import com.frontiercommand.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Camp2WebSocket - Educational content for WebSocket fundamentals
 *
 * This camp teaches real-time bidirectional communication using WebSockets.
 * Users learn about persistent connections, message-based protocols, and
 * real-time data streaming.
 *
 * **Learning Objectives:**
 * - Understand WebSocket vs HTTP differences
 * - Learn connection lifecycle management
 * - Practice sending and receiving messages
 * - Handle connection events (connect, disconnect, errors)
 * - Observe real-time server-initiated messages
 *
 * **Interactive Features:**
 * - Connect/Disconnect controls
 * - Live connection status indicator
 * - Message sending interface
 * - Real-time message feed
 * - Automatic heartbeat display
 *
 * @see WebSocketClient for the WebSocket implementation
 */
@Composable
fun Camp2WebSocket(
    modifier: Modifier = Modifier,
    viewModel: Camp2ViewModel = viewModel()
) {
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val messages by viewModel.messages.collectAsState()
    var messageInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val messagesListState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            messagesListState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Camp 2: WebSocket Fundamentals",
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
                    text = "📡 What is WebSocket?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """
                        WebSocket provides full-duplex communication over a single TCP connection.
                        Unlike HTTP's request-response model, WebSocket enables real-time, bidirectional data flow.

                        Key Differences from HTTP:
                        • Persistent connection (not request-response)
                        • Server can push messages to client
                        • Lower latency for real-time updates
                        • Efficient for frequent updates

                        Use Cases:
                        • Chat applications
                        • Live dashboards
                        • Real-time notifications
                        • Multiplayer games
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Connection Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when (connectionStatus) {
                    ConnectionStatus.CONNECTED -> MaterialTheme.colorScheme.primaryContainer
                    ConnectionStatus.CONNECTING -> MaterialTheme.colorScheme.secondaryContainer
                    ConnectionStatus.DISCONNECTED -> MaterialTheme.colorScheme.surfaceVariant
                    ConnectionStatus.ERROR -> MaterialTheme.colorScheme.errorContainer
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
                            text = "Connection Status",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val (statusText, statusIcon) = when (connectionStatus) {
                                ConnectionStatus.CONNECTED -> "🟢 Connected" to "Connected"
                                ConnectionStatus.CONNECTING -> "🟡 Connecting..." to "Connecting"
                                ConnectionStatus.DISCONNECTED -> "⚫ Disconnected" to "Disconnected"
                                ConnectionStatus.ERROR -> "🔴 Error" to "Error"
                            }
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (connectionStatus == ConnectionStatus.DISCONNECTED) {
                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.connect()
                                }
                            }
                        ) {
                            Text("Connect")
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                viewModel.disconnect()
                            },
                            enabled = connectionStatus != ConnectionStatus.CONNECTING
                        ) {
                            Text("Disconnect")
                        }
                    }
                }
            }
        }

        // Message Feed
        Text(
            text = "Message Feed",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            colors = CardDefaults.cardColors(
                containerColor = androidx.compose.ui.graphics.Color(0xFF1E1E1E)
            )
        ) {
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Connect to see messages...",
                        color = androidx.compose.ui.graphics.Color(0xFF888888),
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                LazyColumn(
                    state = messagesListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { message ->
                        WebSocketMessageItem(message)
                    }
                }
            }
        }

        // Send Message Section
        if (connectionStatus == ConnectionStatus.CONNECTED) {
            Text(
                text = "Send Message",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        if (messageInput.isNotBlank()) {
                            scope.launch {
                                viewModel.sendMessage(messageInput)
                                messageInput = ""
                            }
                        }
                    },
                    enabled = messageInput.isNotBlank(),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (messageInput.isNotBlank()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Icon(Icons.Default.Send, "Send message")
                }
            }

            // Quick commands
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            viewModel.sendMessage("status")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Get Status", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            viewModel.sendMessage("subscribe events")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Subscribe", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            viewModel.sendMessage("ping")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ping", style = MaterialTheme.typography.labelSmall)
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
                        • WebSocket enables real-time bidirectional communication
                        • Connection lifecycle: connect → send/receive → disconnect
                        • Server can push messages without client request
                        • Lower latency than HTTP polling
                        • Ideal for real-time applications

                        Try connecting and sending messages above!
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun WebSocketMessageItem(
    message: WebSocketMessage,
    modifier: Modifier = Modifier
) {
    val color = when (message.type) {
        "system" -> androidx.compose.ui.graphics.Color(0xFF00AAFF)
        "echo" -> androidx.compose.ui.graphics.Color(0xFFFFAA00)
        "response" -> androidx.compose.ui.graphics.Color(0xFF00FF00)
        "notification" -> androidx.compose.ui.graphics.Color(0xFFFF00FF)
        "heartbeat" -> androidx.compose.ui.graphics.Color(0xFFFF6666)
        else -> androidx.compose.ui.graphics.Color(0xFFCCCCCC)
    }

    Column(modifier = modifier) {
        Text(
            text = "[${message.type}]",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = message.content,
            style = MaterialTheme.typography.bodySmall,
            color = androidx.compose.ui.graphics.Color(0xFFEEEEEE),
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * Camp2ViewModel - Manages state for Camp 2
 */
class Camp2ViewModel(application: Application) : BaseViewModel(application) {

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val _messages = MutableStateFlow<List<WebSocketMessage>>(emptyList())
    val messages: StateFlow<List<WebSocketMessage>> = _messages.asStateFlow()

    init {
        // Observe WebSocket status and messages
        launchSafe {
            WebSocketClient.connectionStatus.collect { status ->
                _connectionStatus.value = status
            }
        }

        launchSafe {
            WebSocketClient.incomingMessages.collect { message ->
                if (message != null) {
                    _messages.value = _messages.value + message
                }
            }
        }
    }

    suspend fun connect() {
        try {
            logInfo("Connecting to WebSocket...")
            _messages.value = emptyList() // Clear old messages
            WebSocketClient.connect()
        } catch (e: Exception) {
            logError("Connection failed", e)
        }
    }

    fun disconnect() {
        try {
            logInfo("Disconnecting from WebSocket...")
            WebSocketClient.disconnect()
        } catch (e: Exception) {
            logError("Disconnect failed", e)
        }
    }

    suspend fun sendMessage(message: String) {
        try {
            logInfo("Sending message: $message")
            WebSocketClient.sendMessage(message)
        } catch (e: Exception) {
            logError("Send message failed", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}

@Preview(showBackground = true)
@Composable
fun Camp2WebSocketPreview() {
    PioneerTheme {
        Surface {
            Camp2WebSocket()
        }
    }
}
