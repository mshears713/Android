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
import com.frontiercommand.repository.BackgroundWorkManager
import com.frontiercommand.repository.LogManager
import com.frontiercommand.repository.WorkStatus
import kotlinx.coroutines.launch

/**
 * Camp 8: Background Processing with WorkManager
 *
 * **Educational Goals:**
 * - Understand Android background processing limitations
 * - Master WorkManager for guaranteed background execution
 * - Learn work constraints and policies
 * - Implement periodic and one-time tasks
 * - Monitor work status and handle failures
 * - Follow background execution best practices
 *
 * **Key Concepts Covered:**
 * 1. **WorkManager** - Modern background task solution
 * 2. **Work Constraints** - Network, charging, battery conditions
 * 3. **Work Policies** - Unique work, chaining, retry policies
 * 4. **Work Types** - OneTime, Periodic, Expedited
 * 5. **Work Status** - Enqueued, Running, Succeeded, Failed, Cancelled
 * 6. **Worker Classes** - CoroutineWorker for suspend functions
 *
 * **Background Work Use Cases:**
 * - Syncing data with server
 * - Uploading files
 * - Periodic health checks
 * - Data cleanup and optimization
 * - Processing large datasets
 * - Scheduled notifications
 *
 * This camp demonstrates production-ready background processing
 * patterns essential for building robust Android applications that
 * work reliably even when the app is closed or device is restarted.
 */
@Composable
fun Camp8BackgroundProcessing() {
    val context = LocalContext.current
    val logManager = remember { LogManager.getInstance(context) }
    val backgroundWorkManager = remember { BackgroundWorkManager(context) }
    val scope = rememberCoroutineScope()

    // Track when camp is opened
    LaunchedEffect(Unit) {
        logManager.info("Camp8", "Background Processing camp opened")
    }

    // State
    val workStatuses by backgroundWorkManager.workStatuses.collectAsState()
    var selectedConstraints by remember { mutableStateOf(setOf("network")) }

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
                        text = "🏕️ Camp 8: Background Processing",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Master WorkManager for reliable background tasks",
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
                        text = "📚 Background Processing Fundamentals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        **Why WorkManager?**

                        Android has strict background execution limits to preserve
                        battery life and system resources. WorkManager is the
                        recommended solution for deferrable, guaranteed background work.

                        **WorkManager Benefits:**

                        ✓ Guaranteed execution (survives app closure & reboots)
                        ✓ Respects system constraints (battery, network, etc.)
                        ✓ Handles API level differences automatically
                        ✓ Supports work chaining and dependencies
                        ✓ Built-in retry and backoff policies
                        ✓ Observable work status
                        ✓ Integration with LiveData/Flow

                        **When to Use WorkManager:**

                        • Syncing data with server
                        • Uploading photos/videos
                        • Periodic database cleanup
                        • Downloading content for offline use
                        • Processing large datasets
                        • Scheduled notifications

                        **When NOT to Use WorkManager:**

                        • Immediate execution needed → Use coroutines
                        • Short-lived tasks → Use coroutines
                        • Foreground service needed → Use ForegroundService
                        • Exact timing required → Use AlarmManager

                        **Work Types:**

                        1. **OneTimeWorkRequest**
                           - Executes once
                           - Can be chained
                           - Supports constraints

                        2. **PeriodicWorkRequest**
                           - Repeats at intervals (minimum 15 minutes)
                           - Great for syncs and health checks
                           - Respects Doze mode

                        3. **ExpeditedWorkRequest**
                           - High priority work
                           - Runs quickly if possible
                           - Falls back to regular work if needed

                        **Work Constraints:**

                        • NetworkType: CONNECTED, UNMETERED, NOT_ROAMING
                        • BatteryNotLow: true/false
                        • RequiresCharging: true/false
                        • DeviceIdle: true/false (Doze mode)
                        • StorageNotLow: true/false
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Constraints Selection
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚙️ Work Constraints",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Select constraints for background work:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Network constraint
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedConstraints.contains("network"),
                            onCheckedChange = { checked ->
                                selectedConstraints = if (checked) {
                                    selectedConstraints + "network"
                                } else {
                                    selectedConstraints - "network"
                                }
                            }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Requires Network",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Work only runs when network is available",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Charging constraint
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedConstraints.contains("charging"),
                            onCheckedChange = { checked ->
                                selectedConstraints = if (checked) {
                                    selectedConstraints + "charging"
                                } else {
                                    selectedConstraints - "charging"
                                }
                            }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Requires Charging",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Work only runs when device is charging",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Schedule Work Section
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚀 Schedule Background Work",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // One-time sync
                    Button(
                        onClick = {
                            scope.launch {
                                val workId = backgroundWorkManager.scheduleOneTimeSync(
                                    requiresNetwork = selectedConstraints.contains("network"),
                                    requiresCharging = selectedConstraints.contains("charging")
                                )
                                logManager.info("Camp8", "Scheduled one-time sync: $workId")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Schedule One-Time Sync")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Periodic sync
                    Button(
                        onClick = {
                            scope.launch {
                                val workId = backgroundWorkManager.schedulePeriodicSync(
                                    intervalMinutes = 15,
                                    requiresNetwork = selectedConstraints.contains("network")
                                )
                                logManager.info("Camp8", "Scheduled periodic sync: $workId")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Schedule Periodic Sync (15 min)")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Data cleanup
                    Button(
                        onClick = {
                            scope.launch {
                                val workId = backgroundWorkManager.scheduleDataCleanup()
                                logManager.info("Camp8", "Scheduled data cleanup: $workId")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Schedule Data Cleanup")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cancel all work
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                backgroundWorkManager.cancelAllWork()
                                logManager.info("Camp8", "Cancelled all work")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancel All Work")
                    }
                }
            }
        }

        // Work Status Section
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📊 Work Status (${workStatuses.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (workStatuses.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = "No work scheduled yet",
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        workStatuses.forEach { (workId, status) ->
                            WorkStatusCard(workId, status)
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
                        1. **Choose Appropriate Work Type**
                           - OneTime for single operations
                           - Periodic for regular syncs (min 15 min)
                           - Expedited for urgent work

                        2. **Set Proper Constraints**
                           - Require network for uploads/downloads
                           - Require charging for heavy processing
                           - Consider battery level for user experience

                        3. **Handle Failures Gracefully**
                           - Return Result.retry() for transient failures
                           - Return Result.failure() for permanent failures
                           - Implement backoff policies

                        4. **Use Unique Work Names**
                           - REPLACE for updates
                           - KEEP for deduplication
                           - APPEND for multiple instances

                        5. **Monitor Work Progress**
                           - Use setProgress() for updates
                           - Observe WorkInfo LiveData/Flow
                           - Update UI based on status

                        6. **Optimize Battery Usage**
                           - Batch operations when possible
                           - Use appropriate constraints
                           - Avoid unnecessary wakeups

                        7. **Test Thoroughly**
                           - Test all work types
                           - Verify constraint behavior
                           - Test cancellation scenarios
                           - Simulate network failures
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
                        ✓ WorkManager guarantees background execution
                        ✓ Constraints ensure work runs at right time
                        ✓ Survives app closure and device reboots
                        ✓ CoroutineWorker integrates with coroutines
                        ✓ Observable work status for UI updates
                        ✓ Built-in retry and backoff policies
                        ✓ Respects battery and system resources

                        **Next Steps:**
                        - Explore Camp 9 for system integration
                        - Implement background sync in your apps
                        - Study work chaining patterns
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Code Example
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💻 Code Example",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = """
                            // Define Worker
                            class SyncWorker(context: Context, params: WorkerParameters)
                                : CoroutineWorker(context, params) {

                                override suspend fun doWork(): Result {
                                    return try {
                                        // Perform sync operation
                                        val data = fetchDataFromServer()
                                        saveToDatabase(data)

                                        Result.success()
                                    } catch (e: Exception) {
                                        if (shouldRetry(e)) {
                                            Result.retry()
                                        } else {
                                            Result.failure()
                                        }
                                    }
                                }
                            }

                            // Schedule Work
                            val constraints = Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .setRequiresBatteryNotLow(true)
                                .build()

                            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                                .setConstraints(constraints)
                                .setBackoffCriteria(
                                    BackoffPolicy.EXPONENTIAL,
                                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                    TimeUnit.MILLISECONDS
                                )
                                .build()

                            WorkManager.getInstance(context).enqueue(syncRequest)

                            // Observe Status
                            WorkManager.getInstance(context)
                                .getWorkInfoByIdLiveData(syncRequest.id)
                                .observe(lifecycleOwner) { workInfo ->
                                    when (workInfo.state) {
                                        WorkInfo.State.SUCCEEDED -> showSuccess()
                                        WorkInfo.State.FAILED -> showError()
                                        WorkInfo.State.RUNNING -> showProgress()
                                        else -> {}
                                    }
                                }
                            """.trimIndent(),
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
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
 * WorkStatusCard - Displays work status
 */
@Composable
fun WorkStatusCard(workId: String, status: WorkStatus) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = when (status) {
            WorkStatus.SUCCEEDED -> MaterialTheme.colorScheme.primaryContainer
            WorkStatus.RUNNING -> MaterialTheme.colorScheme.secondaryContainer
            WorkStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
            WorkStatus.CANCELLED -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.tertiaryContainer
        },
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
                    text = workId.take(30) + if (workId.length > 30) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${status.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status indicator
            Surface(
                color = when (status) {
                    WorkStatus.SUCCEEDED -> MaterialTheme.colorScheme.primary
                    WorkStatus.RUNNING -> MaterialTheme.colorScheme.secondary
                    WorkStatus.FAILED -> MaterialTheme.colorScheme.error
                    WorkStatus.CANCELLED -> MaterialTheme.colorScheme.outline
                    else -> MaterialTheme.colorScheme.tertiary
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = when (status) {
                        WorkStatus.SUCCEEDED -> "✓"
                        WorkStatus.RUNNING -> "⟳"
                        WorkStatus.FAILED -> "✗"
                        WorkStatus.CANCELLED -> "⊘"
                        else -> "◷"
                    },
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = when (status) {
                        WorkStatus.SUCCEEDED -> MaterialTheme.colorScheme.onPrimary
                        WorkStatus.RUNNING -> MaterialTheme.colorScheme.onSecondary
                        WorkStatus.FAILED -> MaterialTheme.colorScheme.onError
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}
