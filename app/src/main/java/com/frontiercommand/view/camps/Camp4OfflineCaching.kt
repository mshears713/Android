package com.frontiercommand.view.camps

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.frontiercommand.repository.NetworkClient
import com.frontiercommand.repository.StorageManager
import com.frontiercommand.ui.theme.PioneerTheme
import com.frontiercommand.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Camp4OfflineCaching - Educational content for offline data caching
 *
 * This camp teaches local data persistence strategies, enabling apps to
 * work offline by caching network responses and user data.
 *
 * **Learning Objectives:**
 * - Understand offline-first app architecture
 * - Learn JSON file storage with StorageManager
 * - Implement caching strategies
 * - Handle cached vs fresh data
 * - Manage cache invalidation
 *
 * **Interactive Features:**
 * - Fetch and cache network data
 * - View cached data
 * - Cache management (save, load, delete)
 * - File listing
 * - Cache statistics
 *
 * @see StorageManager for the storage implementation
 */
@Composable
fun Camp4OfflineCaching(
    modifier: Modifier = Modifier,
    viewModel: Camp4ViewModel = viewModel()
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
            text = "Camp 4: Offline Data Caching",
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
                    text = "💾 What is Caching?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """
                        Caching stores data locally so apps work without internet connection.
                        This improves performance and user experience.

                        Key Concepts:
                        • Cache: Local copy of remote data
                        • Cache Hit: Data found locally
                        • Cache Miss: Data must be fetched
                        • Cache Invalidation: Removing stale data

                        Benefits:
                        • Faster app startup
                        • Works offline
                        • Reduces network usage
                        • Better user experience
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Fetch and Cache Section
        Text(
            text = "Fetch & Cache Data",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Step 1: Fetch data from network and cache it locally",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.fetchAndCache("/status", "status_cache.json")
                            }
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CloudDownload, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Fetch Status")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.fetchAndCache("/devices", "devices_cache.json")
                            }
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CloudDownload, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Fetch Devices")
                    }
                }
            }
        }

        // Cache Status
        if (uiState.lastOperation != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.operationSuccess) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (uiState.operationSuccess) "✅ Success" else "❌ Error",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.lastOperation ?: "",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Divider()

        // Load from Cache Section
        Text(
            text = "Load Cached Data",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Step 2: Load previously cached data (works offline)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                viewModel.loadFromCache("status_cache.json")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Load Status")
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                viewModel.loadFromCache("devices_cache.json")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Load Devices")
                    }
                }
            }
        }

        // Cached Data Display
        if (uiState.cachedData != null) {
            Text(
                text = "Cached Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = uiState.cachedData ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }

        Divider()

        // Cache Management
        Text(
            text = "Cache Management",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cached Files: ${uiState.cachedFiles.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                if (uiState.cachedFiles.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.cachedFiles.forEach { filename ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📄 $filename",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        viewModel.deleteCache(filename)
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    "Delete",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                viewModel.refreshFileList()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Refresh List")
                    }

                    if (uiState.cachedFiles.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    viewModel.clearAllCache()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Clear All")
                        }
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
                        • Caching enables offline functionality
                        • Store network responses locally
                        • Check cache before network request
                        • Invalidate stale cache periodically
                        • JSON files are simple and readable

                        Pattern: Fetch → Cache → Load → Display
                        Always handle cache misses gracefully!
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Camp4ViewModel - Manages state for Camp 4
 */
class Camp4ViewModel(application: Application) : BaseViewModel(application) {

    private val storageManager = StorageManager(application)

    data class UiState(
        val isLoading: Boolean = false,
        val lastOperation: String? = null,
        val operationSuccess: Boolean = false,
        val cachedData: String? = null,
        val cachedFiles: List<String> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refreshFileList()
    }

    fun fetchAndCache(endpoint: String, filename: String) {
        launchSafe {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                logInfo("Fetching from network: $endpoint")
                val data = NetworkClient.get(endpoint)

                logInfo("Caching to file: $filename")
                val success = storageManager.saveJson(filename, data)

                if (success) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lastOperation = "Successfully fetched and cached $endpoint to $filename",
                        operationSuccess = true,
                        cachedData = data
                    )
                    refreshFileList()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lastOperation = "Failed to cache data to $filename",
                        operationSuccess = false
                    )
                }
            } catch (e: Exception) {
                logError("Error fetching and caching", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastOperation = "Error: ${e.message}",
                    operationSuccess = false
                )
            }
        }
    }

    fun loadFromCache(filename: String) {
        launchSafe {
            try {
                logInfo("Loading from cache: $filename")
                val data = storageManager.loadJson(filename)

                if (data != null) {
                    _uiState.value = _uiState.value.copy(
                        lastOperation = "Loaded cached data from $filename",
                        operationSuccess = true,
                        cachedData = data
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        lastOperation = "Cache miss: $filename not found",
                        operationSuccess = false,
                        cachedData = null
                    )
                }
            } catch (e: Exception) {
                logError("Error loading from cache", e)
                _uiState.value = _uiState.value.copy(
                    lastOperation = "Error loading cache: ${e.message}",
                    operationSuccess = false
                )
            }
        }
    }

    fun deleteCache(filename: String) {
        launchSafe {
            try {
                logInfo("Deleting cache: $filename")
                val success = storageManager.deleteFile(filename)

                if (success) {
                    _uiState.value = _uiState.value.copy(
                        lastOperation = "Deleted cache file: $filename",
                        operationSuccess = true
                    )
                    refreshFileList()
                } else {
                    _uiState.value = _uiState.value.copy(
                        lastOperation = "Failed to delete: $filename",
                        operationSuccess = false
                    )
                }
            } catch (e: Exception) {
                logError("Error deleting cache", e)
            }
        }
    }

    fun clearAllCache() {
        launchSafe {
            try {
                logInfo("Clearing all cache")
                val count = storageManager.clearAllFiles()

                _uiState.value = _uiState.value.copy(
                    lastOperation = "Cleared $count cached files",
                    operationSuccess = true,
                    cachedData = null
                )
                refreshFileList()
            } catch (e: Exception) {
                logError("Error clearing cache", e)
            }
        }
    }

    fun refreshFileList() {
        launchSafe {
            try {
                val files = storageManager.listFiles()
                _uiState.value = _uiState.value.copy(cachedFiles = files)
                logInfo("Found ${files.size} cached files")
            } catch (e: Exception) {
                logError("Error listing files", e)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Camp4OfflineCachingPreview() {
    PioneerTheme {
        Surface {
            Camp4OfflineCaching()
        }
    }
}
