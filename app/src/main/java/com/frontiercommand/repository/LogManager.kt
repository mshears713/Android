package com.frontiercommand.repository

import android.content.Context
import com.frontiercommand.model.LogEntry
import com.frontiercommand.model.LogLevel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * LogManager - Centralized logging system for the Frontier Command Center
 *
 * This singleton manages all application logging, providing:
 * - Unified logging interface accessible from anywhere in the app
 * - Real-time log streaming via StateFlow for UI display
 * - Persistent storage of logs to JSON files
 * - Log filtering by severity level
 * - Thread-safe concurrent access
 * - Memory-efficient log rotation (limits in-memory logs)
 *
 * **Usage Example:**
 * ```kotlin
 * LogManager.getInstance(context).info("MyComponent", "User logged in successfully")
 * LogManager.getInstance(context).error("NetworkClient", "Connection failed", exception)
 * ```
 *
 * **Architecture:**
 * - Singleton pattern ensures single source of truth for all logs
 * - StateFlow exposes reactive log stream for UI components
 * - Background coroutine scope handles async file I/O
 * - ConcurrentLinkedQueue provides thread-safe log queue
 * - Automatic log rotation prevents unbounded memory growth
 *
 * **Log Lifecycle:**
 * 1. Component calls log method (debug, info, warning, error)
 * 2. LogEntry created and added to in-memory queue
 * 3. StateFlow updated, triggering UI recomposition
 * 4. Log asynchronously persisted to JSON file
 * 5. Rotation triggered if max log count exceeded
 *
 * @property context Application context for file I/O
 */
class LogManager private constructor(private val context: Context) {

    // Thread-safe queue for storing logs in memory
    private val logQueue = ConcurrentLinkedQueue<LogEntry>()

    // StateFlow exposing logs to UI components
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    // Storage manager for persisting logs to JSON
    private val storageManager = StorageManager(context)

    // Coroutine scope for background log operations
    private val logScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // JSON serializer for log persistence
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    // Filter configuration
    private val _minLogLevel = MutableStateFlow(LogLevel.DEBUG)
    val minLogLevel: StateFlow<LogLevel> = _minLogLevel.asStateFlow()

    init {
        // Load persisted logs on initialization
        loadLogsFromStorage()
    }

    companion object {
        // Singleton instance
        @Volatile
        private var instance: LogManager? = null

        // Maximum number of logs to keep in memory (prevents unbounded growth)
        private const val MAX_LOGS_IN_MEMORY = 1000

        // Maximum logs to keep when rotating
        private const val LOGS_TO_KEEP_AFTER_ROTATION = 500

        // Filename for persistent log storage
        private const val LOG_FILE_NAME = "app_logs.json"

        /**
         * Gets the singleton LogManager instance
         * Thread-safe double-checked locking pattern
         *
         * @param context Application context (only needed on first call)
         * @return LogManager singleton instance
         */
        fun getInstance(context: Context): LogManager {
            return instance ?: synchronized(this) {
                instance ?: LogManager(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Logs a DEBUG level message
     * Use for detailed diagnostic information useful during development
     *
     * @param tag Component or class name generating the log
     * @param message Descriptive message
     */
    fun debug(tag: String, message: String) {
        addLog(LogEntry.debug(tag, message))
    }

    /**
     * Logs an INFO level message
     * Use for general informational messages about app state
     *
     * @param tag Component or class name generating the log
     * @param message Descriptive message
     */
    fun info(tag: String, message: String) {
        addLog(LogEntry.info(tag, message))
    }

    /**
     * Logs a WARNING level message
     * Use for potentially problematic situations that don't prevent functionality
     *
     * @param tag Component or class name generating the log
     * @param message Descriptive message
     */
    fun warning(tag: String, message: String) {
        addLog(LogEntry.warning(tag, message))
    }

    /**
     * Logs an ERROR level message with optional exception
     * Use for error events that might still allow the app to continue
     *
     * @param tag Component or class name generating the log
     * @param message Descriptive message
     * @param throwable Optional exception to include in log
     */
    fun error(tag: String, message: String, throwable: Throwable? = null) {
        addLog(LogEntry.error(tag, message, throwable))
    }

    /**
     * Adds a log entry to the queue and triggers persistence
     * Thread-safe operation using concurrent queue
     *
     * @param entry The LogEntry to add
     */
    private fun addLog(entry: LogEntry) {
        // Add to thread-safe queue
        logQueue.offer(entry)

        // Check if rotation needed (prevent unbounded memory growth)
        if (logQueue.size > MAX_LOGS_IN_MEMORY) {
            rotateLogs()
        }

        // Update StateFlow with filtered logs
        updateLogsStateFlow()

        // Persist asynchronously to storage
        logScope.launch {
            persistLogsToStorage()
        }
    }

    /**
     * Rotates logs by removing oldest entries when limit exceeded
     * Keeps only the most recent LOGS_TO_KEEP_AFTER_ROTATION entries
     */
    private fun rotateLogs() {
        val logsToRemove = logQueue.size - LOGS_TO_KEEP_AFTER_ROTATION
        if (logsToRemove > 0) {
            repeat(logsToRemove) {
                logQueue.poll() // Remove oldest entries (FIFO)
            }
            info("LogManager", "Rotated logs: removed $logsToRemove old entries")
        }
    }

    /**
     * Updates the StateFlow with current filtered logs
     * Applies minimum log level filter
     */
    private fun updateLogsStateFlow() {
        val currentMinLevel = _minLogLevel.value
        val filteredLogs = logQueue.filter { entry ->
            entry.level.ordinal >= currentMinLevel.ordinal
        }.toList()
        _logs.value = filteredLogs
    }

    /**
     * Sets the minimum log level for filtering
     * Only logs at or above this level will be shown
     *
     * @param level The minimum LogLevel to display
     */
    fun setMinLogLevel(level: LogLevel) {
        _minLogLevel.value = level
        updateLogsStateFlow()
        info("LogManager", "Log level filter changed to ${level.name}")
    }

    /**
     * Clears all in-memory and persisted logs
     * Useful for privacy or debugging purposes
     */
    fun clearLogs() {
        logQueue.clear()
        _logs.value = emptyList()

        // Clear persisted logs asynchronously
        logScope.launch {
            try {
                storageManager.deleteFile(LOG_FILE_NAME)
                withContext(Dispatchers.Main) {
                    info("LogManager", "All logs cleared")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error("LogManager", "Failed to clear persisted logs", e)
                }
            }
        }
    }

    /**
     * Searches logs for entries matching a query
     * Searches in tag, message, and exception fields
     *
     * @param query Search string (case-insensitive)
     * @return List of matching LogEntry objects
     */
    fun searchLogs(query: String): List<LogEntry> {
        return logQueue.filter { it.matches(query) }.toList()
    }

    /**
     * Gets logs filtered by specific level
     *
     * @param level The LogLevel to filter by
     * @return List of LogEntry objects at the specified level
     */
    fun getLogsByLevel(level: LogLevel): List<LogEntry> {
        return logQueue.filter { it.level == level }.toList()
    }

    /**
     * Loads logs from persistent storage on initialization
     * Handles errors gracefully, starting with empty logs if file doesn't exist
     */
    private fun loadLogsFromStorage() {
        logScope.launch {
            try {
                val jsonString = storageManager.loadJson(LOG_FILE_NAME)
                if (jsonString != null) {
                    val loadedLogs = json.decodeFromString<List<LogEntry>>(jsonString)

                    // Add loaded logs to queue
                    logQueue.clear()
                    loadedLogs.forEach { logQueue.offer(it) }

                    // Update UI
                    withContext(Dispatchers.Main) {
                        updateLogsStateFlow()
                        debug("LogManager", "Loaded ${loadedLogs.size} logs from storage")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        debug("LogManager", "No persisted logs found, starting fresh")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error("LogManager", "Failed to load logs from storage", e)
                }
            }
        }
    }

    /**
     * Persists current logs to JSON storage
     * Called asynchronously after each log addition
     */
    private suspend fun persistLogsToStorage() {
        try {
            val logsToSave = logQueue.toList()
            val jsonString = json.encodeToString(logsToSave)

            val success = storageManager.saveJson(LOG_FILE_NAME, jsonString)
            if (!success) {
                withContext(Dispatchers.Main) {
                    warning("LogManager", "Failed to persist logs to storage")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                error("LogManager", "Exception while persisting logs", e)
            }
        }
    }

    /**
     * Gets the total count of logs in memory
     *
     * @return Number of log entries currently in memory
     */
    fun getLogCount(): Int = logQueue.size

    /**
     * Gets statistics about logs by level
     *
     * @return Map of LogLevel to count
     */
    fun getLogStatistics(): Map<LogLevel, Int> {
        return LogLevel.values().associateWith { level ->
            logQueue.count { it.level == level }
        }
    }

    /**
     * Exports all logs as JSON string
     * Useful for sharing logs for debugging
     *
     * @return JSON string representation of all logs
     */
    fun exportLogsAsJson(): String {
        return try {
            json.encodeToString(logQueue.toList())
        } catch (e: Exception) {
            error("LogManager", "Failed to export logs as JSON", e)
            "[]"
        }
    }

    /**
     * Cleanup method to be called when app is destroyed
     * Cancels all pending coroutines and persists final logs
     */
    fun shutdown() {
        logScope.launch {
            persistLogsToStorage()
            logScope.cancel()
        }
    }
}
