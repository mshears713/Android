package com.frontiercommand.repository

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

/**
 * BackgroundWorkManager - Manages background task scheduling with WorkManager
 *
 * This class provides a simplified interface for scheduling and monitoring
 * background work using Android's WorkManager API. It demonstrates:
 * - One-time work requests
 * - Periodic work requests
 * - Work constraints (network, charging, etc.)
 * - Work status monitoring
 * - Work cancellation
 *
 * **WorkManager Benefits:**
 * - Guaranteed execution even if app is killed
 * - Respects system constraints (battery, network, etc.)
 * - Handles API level differences automatically
 * - Supports chaining and unique work
 * - Survives device reboots
 *
 * **Use Cases:**
 * - Syncing data with server
 * - Uploading files
 * - Periodic health checks
 * - Background data processing
 * - Scheduled notifications
 *
 * @param context Application context
 */
class BackgroundWorkManager(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)
    private val logManager = LogManager.getInstance(context)

    // Track work statuses
    private val _workStatuses = MutableStateFlow<Map<String, WorkStatus>>(emptyMap())
    val workStatuses: StateFlow<Map<String, WorkStatus>> = _workStatuses.asStateFlow()

    /**
     * Schedules a one-time background sync task
     *
     * @param requiresNetwork Whether network is required
     * @param requiresCharging Whether device must be charging
     * @return Unique work ID
     */
    fun scheduleOneTimeSync(
        requiresNetwork: Boolean = true,
        requiresCharging: Boolean = false
    ): String {
        val workId = "one_time_sync_${System.currentTimeMillis()}"

        // Build constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (requiresNetwork) NetworkType.CONNECTED else NetworkType.NOT_REQUIRED
            )
            .setRequiresCharging(requiresCharging)
            .build()

        // Create work request
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .addTag(workId)
            .setInputData(
                workDataOf(
                    "work_id" to workId,
                    "work_type" to "one_time_sync"
                )
            )
            .build()

        // Enqueue work
        workManager.enqueue(syncRequest)

        // Track status
        updateWorkStatus(workId, WorkStatus.ENQUEUED)
        observeWorkStatus(workId, syncRequest.id)

        logManager.info("BackgroundWork", "Scheduled one-time sync: $workId")

        return workId
    }

    /**
     * Schedules a periodic background task
     *
     * @param intervalMinutes Repeat interval in minutes (minimum 15)
     * @param requiresNetwork Whether network is required
     * @return Unique work ID
     */
    fun schedulePeriodicSync(
        intervalMinutes: Long = 15,
        requiresNetwork: Boolean = true
    ): String {
        val workId = "periodic_sync"

        // Build constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (requiresNetwork) NetworkType.CONNECTED else NetworkType.NOT_REQUIRED
            )
            .build()

        // Create periodic work request (minimum 15 minutes)
        val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = intervalMinutes.coerceAtLeast(15),
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(workId)
            .setInputData(
                workDataOf(
                    "work_id" to workId,
                    "work_type" to "periodic_sync"
                )
            )
            .build()

        // Enqueue as unique work (replaces existing)
        workManager.enqueueUniquePeriodicWork(
            workId,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicRequest
        )

        // Track status
        updateWorkStatus(workId, WorkStatus.ENQUEUED)
        observeWorkStatus(workId, periodicRequest.id)

        logManager.info("BackgroundWork", "Scheduled periodic sync: every ${intervalMinutes}m")

        return workId
    }

    /**
     * Schedules a data cleanup task
     *
     * @return Unique work ID
     */
    fun scheduleDataCleanup(): String {
        val workId = "data_cleanup_${System.currentTimeMillis()}"

        val cleanupRequest = OneTimeWorkRequestBuilder<CleanupWorker>()
            .addTag(workId)
            .setInputData(
                workDataOf(
                    "work_id" to workId,
                    "work_type" to "cleanup"
                )
            )
            .build()

        workManager.enqueue(cleanupRequest)

        updateWorkStatus(workId, WorkStatus.ENQUEUED)
        observeWorkStatus(workId, cleanupRequest.id)

        logManager.info("BackgroundWork", "Scheduled data cleanup: $workId")

        return workId
    }

    /**
     * Cancels a specific work by ID
     *
     * @param workId The work ID to cancel
     */
    fun cancelWork(workId: String) {
        workManager.cancelAllWorkByTag(workId)
        updateWorkStatus(workId, WorkStatus.CANCELLED)
        logManager.info("BackgroundWork", "Cancelled work: $workId")
    }

    /**
     * Cancels all pending work
     */
    fun cancelAllWork() {
        workManager.cancelAllWork()
        _workStatuses.value = emptyMap()
        logManager.info("BackgroundWork", "Cancelled all work")
    }

    /**
     * Gets work info by ID
     *
     * @param workId The work ID
     * @return WorkInfo or null if not found
     */
    suspend fun getWorkInfo(workId: String): WorkInfo? {
        val workInfos = workManager.getWorkInfosByTag(workId).await()
        return workInfos.firstOrNull()
    }

    /**
     * Observes work status and updates StateFlow
     *
     * @param workId Unique work identifier
     * @param uuid WorkManager UUID
     */
    private fun observeWorkStatus(workId: String, uuid: java.util.UUID) {
        workManager.getWorkInfoByIdLiveData(uuid).observeForever { workInfo ->
            if (workInfo != null) {
                val status = when (workInfo.state) {
                    WorkInfo.State.ENQUEUED -> WorkStatus.ENQUEUED
                    WorkInfo.State.RUNNING -> WorkStatus.RUNNING
                    WorkInfo.State.SUCCEEDED -> WorkStatus.SUCCEEDED
                    WorkInfo.State.FAILED -> WorkStatus.FAILED
                    WorkInfo.State.BLOCKED -> WorkStatus.BLOCKED
                    WorkInfo.State.CANCELLED -> WorkStatus.CANCELLED
                }
                updateWorkStatus(workId, status)
            }
        }
    }

    /**
     * Updates work status in StateFlow
     *
     * @param workId Work identifier
     * @param status New status
     */
    private fun updateWorkStatus(workId: String, status: WorkStatus) {
        _workStatuses.value = _workStatuses.value + (workId to status)
    }

    /**
     * Gets all work statuses
     *
     * @return Map of work IDs to statuses
     */
    fun getAllWorkStatuses(): Map<String, WorkStatus> {
        return _workStatuses.value
    }
}

/**
 * WorkStatus - Enumeration of work states
 */
enum class WorkStatus {
    ENQUEUED,
    RUNNING,
    SUCCEEDED,
    FAILED,
    BLOCKED,
    CANCELLED
}

/**
 * SyncWorker - Background worker for data synchronization
 *
 * Demonstrates a typical background task that:
 * - Simulates network sync operation
 * - Reports progress
 * - Handles errors gracefully
 * - Returns success/failure result
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val logManager = LogManager.getInstance(context)

    override suspend fun doWork(): Result {
        return try {
            val workId = inputData.getString("work_id") ?: "unknown"
            val workType = inputData.getString("work_type") ?: "sync"

            logManager.info("SyncWorker", "Starting work: $workId ($workType)")

            // Simulate sync operation
            setProgress(workDataOf("progress" to 0))

            // Simulate network delay
            kotlinx.coroutines.delay(2000)

            setProgress(workDataOf("progress" to 50))

            // Simulate data processing
            kotlinx.coroutines.delay(2000)

            setProgress(workDataOf("progress" to 100))

            logManager.info("SyncWorker", "Completed work: $workId")

            Result.success(
                workDataOf(
                    "result" to "sync_completed",
                    "timestamp" to System.currentTimeMillis()
                )
            )

        } catch (e: Exception) {
            logManager.error("SyncWorker", "Work failed", e)
            Result.failure(
                workDataOf("error" to e.message)
            )
        }
    }
}

/**
 * CleanupWorker - Background worker for data cleanup
 *
 * Demonstrates cleanup operations like:
 * - Deleting old cache files
 * - Removing expired data
 * - Optimizing storage
 */
class CleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val logManager = LogManager.getInstance(context)
    private val storageManager = StorageManager(context)

    override suspend fun doWork(): Result {
        return try {
            val workId = inputData.getString("work_id") ?: "unknown"

            logManager.info("CleanupWorker", "Starting cleanup: $workId")

            // Get all files
            val files = storageManager.listFiles()

            // Simulate cleanup - in real app would delete old cache
            var cleanedCount = 0

            files.forEach { filename ->
                // Skip important files
                if (filename != "app_settings.json" && filename != "app_logs.json") {
                    // In real app: check file age and delete if old
                    kotlinx.coroutines.delay(100) // Simulate work
                    cleanedCount++
                }
            }

            logManager.info("CleanupWorker", "Cleanup completed: $cleanedCount files processed")

            Result.success(
                workDataOf(
                    "files_processed" to cleanedCount,
                    "timestamp" to System.currentTimeMillis()
                )
            )

        } catch (e: Exception) {
            logManager.error("CleanupWorker", "Cleanup failed", e)
            Result.failure(
                workDataOf("error" to e.message)
            )
        }
    }
}
