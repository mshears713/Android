package com.frontiercommand.view.camps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.frontiercommand.model.Camp
import com.frontiercommand.repository.LogManager
import com.frontiercommand.repository.StorageManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Camp 7: Extended Data Persistence Strategies
 *
 * **Educational Goals:**
 * - Master advanced data persistence patterns
 * - Implement data synchronization strategies
 * - Handle offline-first architecture
 * - Learn caching best practices
 * - Manage data versioning and migration
 * - Implement conflict resolution
 *
 * **Key Concepts Covered:**
 * 1. **Offline-First Design** - App works without network
 * 2. **Data Synchronization** - Sync local and remote data
 * 3. **Caching Strategies** - Write-through, write-back, cache-aside
 * 4. **Data Versioning** - Handle schema changes gracefully
 * 5. **Conflict Resolution** - Merge conflicting data updates
 * 6. **Storage Optimization** - Compress, index, and clean data
 *
 * **Persistence Patterns:**
 * - Cache-Aside: Load from cache, fetch on miss
 * - Write-Through: Write to cache and storage simultaneously
 * - Write-Back: Write to cache, sync to storage later
 * - Read-Through: Automatic cache population on read
 *
 * This camp demonstrates production-ready data persistence strategies
 * essential for building robust, offline-capable Android applications.
 */
@Composable
fun Camp7DataPersistence() {
    val context = LocalContext.current
    val logManager = remember { LogManager.getInstance(context) }
    val storageManager = remember { StorageManager(context) }
    val scope = rememberCoroutineScope()

    // Track when camp is opened
    LaunchedEffect(Unit) {
        logManager.info("Camp7", "Data Persistence camp opened")
    }

    // Demo state
    var savedItems by remember { mutableStateOf<List<PersistentItem>>(emptyList()) }
    var newItemName by remember { mutableStateOf("") }
    var cacheStats by remember { mutableStateOf(CacheStats()) }
    var syncStatus by remember { mutableStateOf("Idle") }
    var lastSyncTime by remember { mutableStateOf<Long?>(null) }

    // Load items on startup
    LaunchedEffect(Unit) {
        loadItemsFromStorage(storageManager, logManager) { items ->
            savedItems = items
            updateCacheStats(storageManager) { stats ->
                cacheStats = stats
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Camp Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🏕️ Camp 7: Data Persistence",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Master advanced caching and offline strategies",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Tutorial Section
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📚 Persistence Fundamentals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        **Why Offline-First?**

                        Offline-first apps provide a superior user experience:
                        • Work without network connectivity
                        • Instant response with local data
                        • Resilient to network failures
                        • Reduced server load
                        • Better privacy (data stays local)

                        **Caching Strategies:**

                        1. **Cache-Aside (Lazy Loading)**
                           - Check cache first
                           - Load from storage on miss
                           - Update cache with loaded data
                           - Best for: Read-heavy workloads

                        2. **Write-Through**
                           - Write to cache and storage together
                           - Data always consistent
                           - Higher write latency
                           - Best for: Critical data

                        3. **Write-Back (Write-Behind)**
                           - Write to cache immediately
                           - Async sync to storage later
                           - Fast writes, eventual consistency
                           - Best for: High write throughput

                        **Data Versioning:**

                        Track schema versions to handle migrations:
                        - Add version field to data models
                        - Check version on load
                        - Apply migrations for old versions
                        - Preserve backward compatibility

                        **Conflict Resolution:**

                        When local and remote data differ:
                        • Last-Write-Wins: Use newest timestamp
                        • Server-Wins: Remote always preferred
                        • Client-Wins: Local always preferred
                        • Manual: Prompt user to choose
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Cache Statistics
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📊 Cache Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatCard("Items Cached", savedItems.size.toString())
                        StatCard("Cache Size", cacheStats.getFormattedSize())
                        StatCard("Files", cacheStats.fileCount.toString())
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                updateCacheStats(storageManager) { stats ->
                                    cacheStats = stats
                                }
                                logManager.info("Camp7", "Cache stats refreshed")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Refresh Statistics")
                    }
                }
            }
        }

        // Interactive Demo - Add Items
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎯 Demo: Offline Storage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add items to demonstrate persistent storage",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newItemName,
                            onValueChange = { newItemName = it },
                            label = { Text("Item name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (newItemName.isNotBlank()) {
                                    scope.launch {
                                        val newItem = PersistentItem(
                                            id = System.currentTimeMillis().toString(),
                                            name = newItemName,
                                            timestamp = System.currentTimeMillis(),
                                            syncStatus = SyncStatus.PENDING
                                        )

                                        val updatedItems = savedItems + newItem
                                        savedItems = updatedItems

                                        // Save to storage (Write-Through pattern)
                                        saveItemsToStorage(storageManager, updatedItems, logManager)

                                        newItemName = ""
                                        logManager.info("Camp7", "Item added: ${newItem.name}")

                                        // Update cache stats
                                        updateCacheStats(storageManager) { stats ->
                                            cacheStats = stats
                                        }
                                    }
                                }
                            },
                            enabled = newItemName.isNotBlank()
                        ) {
                            Text("Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sync controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sync Status: $syncStatus",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Button(
                            onClick = {
                                scope.launch {
                                    syncStatus = "Syncing..."
                                    logManager.info("Camp7", "Starting sync")

                                    // Simulate sync delay
                                    delay(2000)

                                    // Mark all as synced
                                    val syncedItems = savedItems.map {
                                        it.copy(
                                            syncStatus = SyncStatus.SYNCED,
                                            lastSyncTime = System.currentTimeMillis()
                                        )
                                    }
                                    savedItems = syncedItems
                                    saveItemsToStorage(storageManager, syncedItems, logManager)

                                    lastSyncTime = System.currentTimeMillis()
                                    syncStatus = "Synced"
                                    logManager.info("Camp7", "Sync completed")
                                }
                            },
                            enabled = savedItems.any { it.syncStatus == SyncStatus.PENDING }
                        ) {
                            Text("Sync")
                        }
                    }

                    if (lastSyncTime != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Last synced: ${formatTimestamp(lastSyncTime!!)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Saved Items List
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💾 Saved Items (${savedItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (savedItems.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        savedItems = emptyList()
                                        saveItemsToStorage(storageManager, emptyList(), logManager)
                                        updateCacheStats(storageManager) { stats ->
                                            cacheStats = stats
                                        }
                                        logManager.info("Camp7", "All items cleared")
                                    }
                                }
                            ) {
                                Text("Clear All")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (savedItems.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = "No items saved yet. Add some above!",
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        savedItems.forEach { item ->
                            PersistentItemCard(
                                item = item,
                                onDelete = {
                                    scope.launch {
                                        val updatedItems = savedItems.filter { it.id != item.id }
                                        savedItems = updatedItems
                                        saveItemsToStorage(storageManager, updatedItems, logManager)
                                        updateCacheStats(storageManager) { stats ->
                                            cacheStats = stats
                                        }
                                        logManager.info("Camp7", "Item deleted: ${item.name}")
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Best Practices
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "✅ Best Practices",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        1. **Always Handle Errors**
                           - Wrap I/O in try-catch
                           - Provide fallback values
                           - Log errors for debugging

                        2. **Use Dispatchers.IO**
                           - Never block main thread
                           - Use suspend functions
                           - Leverage coroutines

                        3. **Implement Versioning**
                           - Add version field to models
                           - Check version on load
                           - Migrate old data gracefully

                        4. **Optimize Storage**
                           - Compress large data
                           - Clean up old cache
                           - Implement TTL (time-to-live)

                        5. **Test Thoroughly**
                           - Test offline scenarios
                           - Verify data integrity
                           - Check migration paths

                        6. **Consider Privacy**
                           - Encrypt sensitive data
                           - Clear data on logout
                           - Respect user preferences
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Key Takeaways
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎯 Key Takeaways",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        ✓ Offline-first design improves user experience
                        ✓ Choose caching strategy based on use case
                        ✓ Implement data versioning for migrations
                        ✓ Handle sync conflicts gracefully
                        ✓ Always use background threads for I/O
                        ✓ Test offline scenarios thoroughly
                        ✓ Optimize storage with compression and cleanup

                        **Next Steps:**
                        - Explore Camp 8 for background processing
                        - Implement offline support in your apps
                        - Study conflict resolution strategies
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * PersistentItem - Demo model for persistence
 */
@Serializable
data class PersistentItem(
    val id: String,
    val name: String,
    val timestamp: Long,
    val syncStatus: SyncStatus,
    val lastSyncTime: Long? = null,
    val version: Int = 1
)

/**
 * SyncStatus - Item synchronization state
 */
@Serializable
enum class SyncStatus {
    PENDING,
    SYNCING,
    SYNCED,
    FAILED
}

/**
 * CacheStats - Storage statistics
 */
data class CacheStats(
    val totalSizeBytes: Long = 0,
    val fileCount: Int = 0
) {
    fun getFormattedSize(): String {
        return when {
            totalSizeBytes < 1024 -> "$totalSizeBytes B"
            totalSizeBytes < 1024 * 1024 -> "${totalSizeBytes / 1024} KB"
            else -> "${totalSizeBytes / (1024 * 1024)} MB"
        }
    }
}

/**
 * StatCard - Small statistics display card
 */
@Composable
fun StatCard(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * PersistentItemCard - Displays a saved item
 */
@Composable
fun PersistentItemCard(
    item: PersistentItem,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Created: ${formatTimestamp(item.timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Status: ${item.syncStatus.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = when (item.syncStatus) {
                        SyncStatus.SYNCED -> MaterialTheme.colorScheme.primary
                        SyncStatus.PENDING -> MaterialTheme.colorScheme.tertiary
                        SyncStatus.SYNCING -> MaterialTheme.colorScheme.secondary
                        SyncStatus.FAILED -> MaterialTheme.colorScheme.error
                    }
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Helper function to load items from storage
 */
private suspend fun loadItemsFromStorage(
    storageManager: StorageManager,
    logManager: LogManager,
    onLoaded: (List<PersistentItem>) -> Unit
) {
    try {
        val items = storageManager.loadObject<List<PersistentItem>>("persistent_items.json")
        if (items != null) {
            onLoaded(items)
            logManager.info("Camp7", "Loaded ${items.size} items from storage")
        } else {
            onLoaded(emptyList())
            logManager.info("Camp7", "No saved items found")
        }
    } catch (e: Exception) {
        logManager.error("Camp7", "Failed to load items", e)
        onLoaded(emptyList())
    }
}

/**
 * Helper function to save items to storage
 */
private suspend fun saveItemsToStorage(
    storageManager: StorageManager,
    items: List<PersistentItem>,
    logManager: LogManager
) {
    try {
        val success = storageManager.saveObject("persistent_items.json", items)
        if (success) {
            logManager.info("Camp7", "Saved ${items.size} items to storage")
        } else {
            logManager.warning("Camp7", "Failed to save items")
        }
    } catch (e: Exception) {
        logManager.error("Camp7", "Error saving items", e)
    }
}

/**
 * Helper function to update cache statistics
 */
private suspend fun updateCacheStats(
    storageManager: StorageManager,
    onUpdated: (CacheStats) -> Unit
) {
    try {
        val files = storageManager.listFiles()
        var totalSize = 0L

        files.forEach { filename ->
            val size = storageManager.getFileSize(filename)
            if (size > 0) {
                totalSize += size
            }
        }

        onUpdated(CacheStats(totalSize, files.size))
    } catch (e: Exception) {
        onUpdated(CacheStats(0, 0))
    }
}

/**
 * Helper function to format timestamp
 */
private fun formatTimestamp(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
    return format.format(date)
}
