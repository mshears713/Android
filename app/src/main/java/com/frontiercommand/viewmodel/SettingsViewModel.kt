package com.frontiercommand.viewmodel

import android.app.Application
import android.content.Context
import com.frontiercommand.model.LogEntry
import com.frontiercommand.model.LogLevel
import com.frontiercommand.repository.LogManager
import com.frontiercommand.repository.StorageManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * SettingsViewModel - Manages application settings and configuration
 *
 * This ViewModel handles user preferences, theme configuration, log viewing,
 * and other app-wide settings. It integrates with LogManager and StorageManager
 * to provide a centralized settings interface.
 *
 * **Features:**
 * - Theme selection (Light, Dark, System)
 * - Log viewing and filtering
 * - Log level configuration
 * - Clear cache/logs operations
 * - App information display
 * - Settings persistence
 *
 * **State Management:**
 * - Uses StateFlow for reactive UI updates
 * - Persists settings to JSON file
 * - Exposes read-only StateFlows to UI
 *
 * **Architecture:**
 * ```
 * UI → SettingsViewModel → LogManager/StorageManager
 *                      ↓
 *                 StateFlow → UI
 * ```
 *
 * @param application Application instance for context access
 */
class SettingsViewModel(application: Application) : BaseViewModel(application) {

    private val logManager = LogManager.getInstance(application)
    private val storageManager = StorageManager(application)

    // Settings file name
    private companion object {
        const val SETTINGS_FILE = "app_settings.json"
    }

    // Theme Mode State
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    // Log Filter State
    private val _logFilter = MutableStateFlow("")
    val logFilter: StateFlow<String> = _logFilter.asStateFlow()

    // Selected Log Level Filter
    private val _selectedLogLevel = MutableStateFlow<LogLevel?>(null)
    val selectedLogLevel: StateFlow<LogLevel?> = _selectedLogLevel.asStateFlow()

    // Logs from LogManager
    val logs: StateFlow<List<LogEntry>> = logManager.logs

    // Filtered logs based on search query and level
    val filteredLogs: StateFlow<List<LogEntry>> = combine(
        logs,
        _logFilter,
        _selectedLogLevel
    ) { allLogs, filter, level ->
        var filtered = allLogs

        // Filter by level if specified
        if (level != null) {
            filtered = filtered.filter { it.level == level }
        }

        // Filter by search query
        if (filter.isNotBlank()) {
            filtered = filtered.filter { it.matches(filter) }
        }

        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Log statistics
    val logStatistics: StateFlow<Map<LogLevel, Int>> = logs.map { allLogs ->
        LogLevel.values().associateWith { level ->
            allLogs.count { it.level == level }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    // Auto-refresh logs
    private val _autoRefreshLogs = MutableStateFlow(true)
    val autoRefreshLogs: StateFlow<Boolean> = _autoRefreshLogs.asStateFlow()

    // Show debug logs
    private val _showDebugLogs = MutableStateFlow(true)
    val showDebugLogs: StateFlow<Boolean> = _showDebugLogs.asStateFlow()

    init {
        logInfo("Initializing SettingsViewModel")
        loadSettings()
    }

    /**
     * Sets the theme mode preference
     *
     * @param mode The desired ThemeMode (LIGHT, DARK, SYSTEM)
     */
    fun setThemeMode(mode: ThemeMode) {
        launchSafe {
            try {
                logInfo("Changing theme mode to: $mode")
                _themeMode.value = mode
                saveSettings()
                logManager.info("Settings", "Theme changed to ${mode.name}")
            } catch (e: Exception) {
                logError("Error setting theme mode", e)
                logManager.error("Settings", "Failed to change theme", e)
            }
        }
    }

    /**
     * Sets the log search filter
     *
     * @param filter Search query string
     */
    fun setLogFilter(filter: String) {
        _logFilter.value = filter
        logDebug("Log filter changed: $filter")
    }

    /**
     * Sets the log level filter
     *
     * @param level LogLevel to filter by, or null for all levels
     */
    fun setLogLevelFilter(level: LogLevel?) {
        _selectedLogLevel.value = level
        logManager.setMinLogLevel(level ?: LogLevel.DEBUG)
        logInfo("Log level filter: ${level?.name ?: "ALL"}")
    }

    /**
     * Clears all application logs
     */
    fun clearLogs() {
        launchSafe {
            try {
                logInfo("Clearing all logs")
                logManager.clearLogs()
                logManager.info("Settings", "All logs cleared")
            } catch (e: Exception) {
                logError("Error clearing logs", e)
                logManager.error("Settings", "Failed to clear logs", e)
            }
        }
    }

    /**
     * Clears application cache
     */
    fun clearCache() {
        launchSafe {
            try {
                logInfo("Clearing application cache")

                // Get list of all files
                val files = storageManager.listFiles()

                // Delete all files except settings and logs
                val filesToDelete = files.filter {
                    it != SETTINGS_FILE && it != "app_logs.json"
                }

                var deletedCount = 0
                filesToDelete.forEach { filename ->
                    if (storageManager.deleteFile(filename)) {
                        deletedCount++
                    }
                }

                logInfo("Cleared cache: $deletedCount files deleted")
                logManager.info("Settings", "Cache cleared: $deletedCount files")

            } catch (e: Exception) {
                logError("Error clearing cache", e)
                logManager.error("Settings", "Failed to clear cache", e)
            }
        }
    }

    /**
     * Exports logs as JSON string
     *
     * @return JSON string containing all logs
     */
    fun exportLogs(): String {
        return try {
            val exported = logManager.exportLogsAsJson()
            logInfo("Logs exported successfully")
            logManager.info("Settings", "Logs exported")
            exported
        } catch (e: Exception) {
            logError("Error exporting logs", e)
            logManager.error("Settings", "Failed to export logs", e)
            "[]"
        }
    }

    /**
     * Gets app storage information
     *
     * @return StorageInfo object with cache size and file count
     */
    fun getStorageInfo(): StateFlow<StorageInfo> {
        val storageInfo = MutableStateFlow(StorageInfo(0, 0, 0))

        launchSafe {
            try {
                val files = storageManager.listFiles()
                val totalFiles = files.size

                var totalSize = 0L
                var cacheFiles = 0

                files.forEach { filename ->
                    val size = storageManager.getFileSize(filename)
                    if (size > 0) {
                        totalSize += size

                        // Count non-settings files as cache
                        if (filename != SETTINGS_FILE && filename != "app_logs.json") {
                            cacheFiles++
                        }
                    }
                }

                storageInfo.value = StorageInfo(
                    totalSizeBytes = totalSize,
                    totalFiles = totalFiles,
                    cacheFiles = cacheFiles
                )

                logDebug("Storage info: ${totalFiles} files, ${totalSize} bytes")

            } catch (e: Exception) {
                logError("Error getting storage info", e)
            }
        }

        return storageInfo.asStateFlow()
    }

    /**
     * Toggles auto-refresh for logs
     */
    fun toggleAutoRefreshLogs() {
        _autoRefreshLogs.value = !_autoRefreshLogs.value
        logInfo("Auto-refresh logs: ${_autoRefreshLogs.value}")
    }

    /**
     * Toggles visibility of debug logs
     */
    fun toggleShowDebugLogs() {
        _showDebugLogs.value = !_showDebugLogs.value

        // Update minimum log level based on debug visibility
        val minLevel = if (_showDebugLogs.value) LogLevel.DEBUG else LogLevel.INFO
        logManager.setMinLogLevel(minLevel)

        logInfo("Show debug logs: ${_showDebugLogs.value}")
    }

    /**
     * Loads settings from persistent storage
     */
    private fun loadSettings() {
        launchSafe {
            try {
                logDebug("Loading settings from storage")

                val settings = storageManager.loadObject<AppSettings>(SETTINGS_FILE)

                if (settings != null) {
                    _themeMode.value = settings.themeMode
                    _autoRefreshLogs.value = settings.autoRefreshLogs
                    _showDebugLogs.value = settings.showDebugLogs

                    logInfo("Settings loaded successfully")
                } else {
                    logInfo("No saved settings found, using defaults")
                }

            } catch (e: Exception) {
                logError("Error loading settings", e)
            }
        }
    }

    /**
     * Saves current settings to persistent storage
     */
    private fun saveSettings() {
        launchSafe {
            try {
                logDebug("Saving settings to storage")

                val settings = AppSettings(
                    themeMode = _themeMode.value,
                    autoRefreshLogs = _autoRefreshLogs.value,
                    showDebugLogs = _showDebugLogs.value
                )

                val success = storageManager.saveObject(SETTINGS_FILE, settings)

                if (success) {
                    logInfo("Settings saved successfully")
                } else {
                    logWarning("Failed to save settings")
                }

            } catch (e: Exception) {
                logError("Error saving settings", e)
            }
        }
    }

    /**
     * Resets all settings to defaults
     */
    fun resetToDefaults() {
        launchSafe {
            try {
                logInfo("Resetting settings to defaults")

                _themeMode.value = ThemeMode.SYSTEM
                _autoRefreshLogs.value = true
                _showDebugLogs.value = true
                _logFilter.value = ""
                _selectedLogLevel.value = null

                saveSettings()
                logManager.info("Settings", "Settings reset to defaults")

            } catch (e: Exception) {
                logError("Error resetting settings", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        logInfo("SettingsViewModel cleared")
    }
}

/**
 * ThemeMode - Enumeration of available theme modes
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * AppSettings - Data class for persisting app settings
 */
@kotlinx.serialization.Serializable
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val autoRefreshLogs: Boolean = true,
    val showDebugLogs: Boolean = true
)

/**
 * StorageInfo - Data class containing storage statistics
 */
data class StorageInfo(
    val totalSizeBytes: Long,
    val totalFiles: Int,
    val cacheFiles: Int
) {
    /**
     * Gets human-readable size string
     */
    fun getFormattedSize(): String {
        return when {
            totalSizeBytes < 1024 -> "$totalSizeBytes B"
            totalSizeBytes < 1024 * 1024 -> "${totalSizeBytes / 1024} KB"
            else -> "${totalSizeBytes / (1024 * 1024)} MB"
        }
    }
}
