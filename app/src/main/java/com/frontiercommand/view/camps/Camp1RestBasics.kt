package com.frontiercommand.view.camps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.frontiercommand.repository.NetworkClient
import com.frontiercommand.repository.NetworkException
import com.frontiercommand.ui.theme.PioneerTheme
import com.frontiercommand.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import android.app.Application

/**
 * Camp1RestBasics - Educational content for REST API fundamentals
 *
 * This camp teaches HTTP networking basics through interactive demonstrations.
 * Users learn about REST APIs, HTTP methods (GET, POST), request-response patterns,
 * and JSON data handling.
 *
 * **Learning Objectives:**
 * - Understand REST API concepts and architecture
 * - Learn HTTP methods: GET for retrieval, POST for submission
 * - Practice making API calls with NetworkClient
 * - Interpret JSON responses
 * - Handle network errors gracefully
 *
 * **Interactive Features:**
 * - "Try It" buttons for each endpoint
 * - Live response display
 * - Error handling demonstrations
 * - Example command syntax
 *
 * @see NetworkClient for the HTTP implementation
 */
@Composable
fun Camp1RestBasics(
    modifier: Modifier = Modifier,
    viewModel: Camp1ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Camp 1: REST API Basics",
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
                    text = "📚 What is REST?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """
                        REST (Representational State Transfer) is an architectural style for building web APIs.
                        It uses standard HTTP methods to perform operations on resources.

                        Key Concepts:
                        • Resources: Data entities (e.g., users, devices, status)
                        • HTTP Methods: GET (read), POST (create), PUT (update), DELETE (remove)
                        • Stateless: Each request contains all needed information
                        • JSON: Common data format for requests and responses
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // GET Method Section
        Text(
            text = "HTTP GET Method",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "GET retrieves data from the server without modifying it. Perfect for reading status, fetching lists, or querying information.",
            style = MaterialTheme.typography.bodyMedium
        )

        // GET Examples
        ApiEndpointDemo(
            title = "GET /status",
            description = "Fetch system status information",
            method = "GET",
            endpoint = "/status",
            onTry = {
                scope.launch {
                    viewModel.executeGet("/status")
                }
            },
            isLoading = uiState.isLoading && uiState.lastEndpoint == "/status"
        )

        ApiEndpointDemo(
            title = "GET /devices",
            description = "Retrieve list of connected devices",
            method = "GET",
            endpoint = "/devices",
            onTry = {
                scope.launch {
                    viewModel.executeGet("/devices")
                }
            },
            isLoading = uiState.isLoading && uiState.lastEndpoint == "/devices"
        )

        ApiEndpointDemo(
            title = "GET /logs",
            description = "Fetch recent log entries",
            method = "GET",
            endpoint = "/logs",
            onTry = {
                scope.launch {
                    viewModel.executeGet("/logs")
                }
            },
            isLoading = uiState.isLoading && uiState.lastEndpoint == "/logs"
        )

        Divider()

        // POST Method Section
        Text(
            text = "HTTP POST Method",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "POST sends data to the server to create or update resources. Includes a request body with data.",
            style = MaterialTheme.typography.bodyMedium
        )

        // POST Examples
        ApiEndpointDemo(
            title = "POST /config",
            description = "Update system configuration",
            method = "POST",
            endpoint = "/config",
            body = """{"key": "value"}""",
            onTry = {
                scope.launch {
                    viewModel.executePost("/config", """{"setting": "updated"}""")
                }
            },
            isLoading = uiState.isLoading && uiState.lastEndpoint == "/config"
        )

        ApiEndpointDemo(
            title = "POST /command",
            description = "Execute a command on the server",
            method = "POST",
            endpoint = "/command",
            body = """{"command": "restart"}""",
            onTry = {
                scope.launch {
                    viewModel.executePost("/command", """{"command": "status"}""")
                }
            },
            isLoading = uiState.isLoading && uiState.lastEndpoint == "/command"
        )

        Divider()

        // Response Display
        if (uiState.lastResponse != null || uiState.lastError != null) {
            Text(
                text = "Latest Response",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.lastError != null) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (uiState.lastError != null) "❌ Error" else "✅ Success",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.lastError ?: uiState.lastResponse ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
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
                        • GET retrieves data, POST sends data
                        • URLs identify resources (endpoints)
                        • JSON is the common data format
                        • Always handle errors gracefully
                        • REST APIs are stateless

                        Try using these in the CommandConsole!
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * ApiEndpointDemo - Reusable component for demonstrating API endpoints
 */
@Composable
fun ApiEndpointDemo(
    title: String,
    description: String,
    method: String,
    endpoint: String,
    body: String? = null,
    onTry: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = onTry,
                    enabled = !isLoading,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Try It")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Code example
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.small
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "$method $endpoint",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (body != null) {
                        Text(
                            text = "Body: $body",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Camp1ViewModel - Manages state for Camp 1
 */
class Camp1ViewModel(application: Application) : BaseViewModel(application) {

    data class UiState(
        val isLoading: Boolean = false,
        val lastResponse: String? = null,
        val lastError: String? = null,
        val lastEndpoint: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    suspend fun executeGet(endpoint: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            lastEndpoint = endpoint,
            lastError = null
        )

        try {
            val response = NetworkClient.get(endpoint)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                lastResponse = response,
                lastError = null
            )
            logInfo("GET $endpoint successful")
        } catch (e: NetworkException) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                lastResponse = null,
                lastError = "Network Error: ${e.message}"
            )
            logError("GET $endpoint failed", e)
        }
    }

    suspend fun executePost(endpoint: String, body: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            lastEndpoint = endpoint,
            lastError = null
        )

        try {
            val response = NetworkClient.post(endpoint, body)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                lastResponse = response,
                lastError = null
            )
            logInfo("POST $endpoint successful")
        } catch (e: NetworkException) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                lastResponse = null,
                lastError = "Network Error: ${e.message}"
            )
            logError("POST $endpoint failed", e)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Camp1RestBasicsPreview() {
    PioneerTheme {
        Surface {
            Camp1RestBasics()
        }
    }
}
