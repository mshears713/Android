package com.frontiercommand.model

import kotlinx.serialization.Serializable

/**
 * LogEntry - Represents a single log event in the application
 *
 * The centralized logging system captures all significant events including:
 * - Navigation events
 * - Network operations (HTTP, WebSocket)
 * - GPS location updates
 * - File I/O operations
 * - User actions (commands, settings changes)
 * - Errors and exceptions
 *
 * Logs are displayed in the Settings screen and can be filtered by severity.
 * They're also persisted to JSON files for debugging and analysis.
 *
 * **Log Levels (by severity):**
 * - DEBUG: Detailed diagnostic information for development
 * - INFO: General informational messages about app state
 * - WARNING: Potentially problematic situations that don't prevent functionality
 * - ERROR: Error events that might still allow the app to continue
 *
 * @property id Unique identifier for this log entry
 * @property timestamp When the event occurred (milliseconds since epoch)
 * @property level Severity level (DEBUG, INFO, WARNING, ERROR)
 * @property tag Component or class that generated the log (e.g., "NetworkClient", "CampViewModel")
 * @property message Human-readable log message
 * @property exception Optional exception details if this is an error log
 */
@Serializable
data class LogEntry(
    val id: String,
    val timestamp: Long,
    val level: LogLevel,
    val tag: String,
    val message: String,
    val exception: String? = null
) {
    /**
     * Formats the log entry as a human-readable string
     * Format: [LEVEL] Tag: Message
     *
     * @return Formatted log string
     */
    fun format(): String {
        val exceptionPart = if (exception != null) "\n  Exception: $exception" else ""
        return "[${level.name}] $tag: $message$exceptionPart"
    }

    /**
     * Checks if this log entry matches a search query
     * Searches in tag, message, and exception fields (case-insensitive)
     *
     * @param query The search string
     * @return true if the query matches any field
     */
    fun matches(query: String): Boolean {
        if (query.isBlank()) return true

        val lowerQuery = query.lowercase()
        return tag.lowercase().contains(lowerQuery) ||
                message.lowercase().contains(lowerQuery) ||
                exception?.lowercase()?.contains(lowerQuery) == true
    }

    companion object {
        /**
         * Creates a DEBUG level log entry
         *
         * @param tag The component generating the log
         * @param message The log message
         * @return New LogEntry with DEBUG level
         */
        fun debug(tag: String, message: String): LogEntry {
            return LogEntry(
                id = generateId(),
                timestamp = System.currentTimeMillis(),
                level = LogLevel.DEBUG,
                tag = tag,
                message = message
            )
        }

        /**
         * Creates an INFO level log entry
         *
         * @param tag The component generating the log
         * @param message The log message
         * @return New LogEntry with INFO level
         */
        fun info(tag: String, message: String): LogEntry {
            return LogEntry(
                id = generateId(),
                timestamp = System.currentTimeMillis(),
                level = LogLevel.INFO,
                tag = tag,
                message = message
            )
        }

        /**
         * Creates a WARNING level log entry
         *
         * @param tag The component generating the log
         * @param message The log message
         * @return New LogEntry with WARNING level
         */
        fun warning(tag: String, message: String): LogEntry {
            return LogEntry(
                id = generateId(),
                timestamp = System.currentTimeMillis(),
                level = LogLevel.WARNING,
                tag = tag,
                message = message
            )
        }

        /**
         * Creates an ERROR level log entry with optional exception
         *
         * @param tag The component generating the log
         * @param message The log message
         * @param throwable Optional exception
         * @return New LogEntry with ERROR level
         */
        fun error(tag: String, message: String, throwable: Throwable? = null): LogEntry {
            return LogEntry(
                id = generateId(),
                timestamp = System.currentTimeMillis(),
                level = LogLevel.ERROR,
                tag = tag,
                message = message,
                exception = throwable?.stackTraceToString()
            )
        }

        /**
         * Generates a unique ID for log entries
         * Format: timestamp-random
         *
         * @return Unique log ID
         */
        private fun generateId(): String {
            return "${System.currentTimeMillis()}-${(0..9999).random()}"
        }
    }
}

/**
 * LogLevel - Enumeration of log severity levels
 *
 * Ordered from least to most severe for filtering purposes
 */
@Serializable
enum class LogLevel {
    DEBUG,
    INFO,
    WARNING,
    ERROR
}
