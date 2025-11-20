package com.frontiercommand.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.frontiercommand.model.LogLevel
import com.frontiercommand.repository.LogManager
import com.frontiercommand.viewmodel.SettingsViewModel
import com.frontiercommand.viewmodel.ThemeMode
import java.text.SimpleDateFormat
import java.util.*

/**
 * SettingsScreen - Application settings and configuration interface
 *
 * Provides access to:
 * - Theme selection (Light/Dark/System)
 * - Log viewer with filtering
 * - Log level configuration
 * - Cache management
 * - Storage information
 * - App information
 *
 * **Features:**
 * - Real-time log viewing with auto-refresh
 * - Filter logs by level and search query
 * - Export logs for debugging
 * - Clear cache and logs
 * - View storage statistics
 *
 * @param navController Navigation controller for back navigation
 * @param viewModel SettingsViewModel managing settings state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val logManager = remember { LogManager.getInstance(context) }

    // Collect state from ViewModel
    val themeMode by viewModel.themeMode.collectAsState()
    val filteredLogs by viewModel.filteredLogs.collectAsState()
    val logFilter by viewModel.logFilter.collectAsState()
    val selectedLogLevel by viewModel.selectedLogLevel.collectAsState()
    val logStatistics by viewModel.logStatistics.collectAsState()
    val storageInfo by viewModel.getStorageInfo().collectAsState()
    val autoRefreshLogs by viewModel.autoRefreshLogs.collectAsState()
    val showDebugLogs by viewModel.showDebugLogs.collectAsState()

    // Track screen open
    LaunchedEffect(Unit) {
        logManager.info("Settings", "Settings screen opened")
    }

    // Expanded sections state
    var logsExpanded by remember { mutableStateOf(false) }
    var storageExpanded by remember { mutableStateOf(false) }
    var aboutExpanded by remember { mutableStateOf(false) }

    // Show confirmation dialogs
    var showClearLogsDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Settings Section
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Theme",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Theme",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ThemeMode.values().forEach { mode ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = themeMode == mode,
                                    onClick = { viewModel.setThemeMode(mode) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = mode.name.lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }

            // Log Viewer Section
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = "Logs",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Application Logs",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(onClick = { logsExpanded = !logsExpanded }) {
                                Icon(
                                    imageVector = if (logsExpanded)
                                        Icons.Default.ExpandLess
                                    else
                                        Icons.Default.ExpandMore,
                                    contentDescription = if (logsExpanded) "Collapse" else "Expand"
                                )
                            }
                        }

                        if (logsExpanded) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Log Statistics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                logStatistics.forEach { (level, count) ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = level.name,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        Text(
                                            text = count.toString(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = when (level) {
                                                LogLevel.ERROR -> MaterialTheme.colorScheme.error
                                                LogLevel.WARNING -> MaterialTheme.colorScheme.tertiary
                                                LogLevel.INFO -> MaterialTheme.colorScheme.primary
                                                LogLevel.DEBUG -> MaterialTheme.colorScheme.secondary
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Search filter
                            OutlinedTextField(
                                value = logFilter,
                                onValueChange = { viewModel.setLogFilter(it) },
                                label = { Text("Search logs") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Default.Search, "Search")
                                },
                                trailingIcon = {
                                    if (logFilter.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.setLogFilter("") }) {
                                            Icon(Icons.Default.Clear, "Clear")
                                        }
                                    }
                                },
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Log level filter chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedLogLevel == null,
                                    onClick = { viewModel.setLogLevelFilter(null) },
                                    label = { Text("All") }
                                )

                                LogLevel.values().forEach { level ->
                                    FilterChip(
                                        selected = selectedLogLevel == level,
                                        onClick = { viewModel.setLogLevelFilter(level) },
                                        label = { Text(level.name) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Log options
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = showDebugLogs,
                                        onCheckedChange = { viewModel.toggleShowDebugLogs() }
                                    )
                                    Text("Show debug logs")
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { showClearLogsDialog = true },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Icon(Icons.Default.Delete, "Clear", Modifier.size(18.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Clear")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Log list
                            Text(
                                text = "Recent Logs (${filteredLogs.size})",
                                style = MaterialTheme.typography.labelLarge
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (filteredLogs.isEmpty()) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(
                                        text = "No logs found",
                                        modifier = Modifier.padding(32.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                filteredLogs.take(10).forEach { log ->
                                    LogEntryCard(log)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                if (filteredLogs.size > 10) {
                                    Text(
                                        text = "+ ${filteredLogs.size - 10} more logs",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Storage Management Section
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = "Storage",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Storage",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(onClick = { storageExpanded = !storageExpanded }) {
                                Icon(
                                    imageVector = if (storageExpanded)
                                        Icons.Default.ExpandLess
                                    else
                                        Icons.Default.ExpandMore,
                                    contentDescription = if (storageExpanded) "Collapse" else "Expand"
                                )
                            }
                        }

                        if (storageExpanded) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Storage stats
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Total Size",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = storageInfo.getFormattedSize(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Files",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = storageInfo.totalFiles.toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Cache Files",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = storageInfo.cacheFiles.toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { showClearCacheDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(Icons.Default.Delete, "Clear cache")
                                Spacer(Modifier.width(8.dp))
                                Text("Clear Cache")
                            }
                        }
                    }
                }
            }

            // About Section
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "About",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "About",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(onClick = { aboutExpanded = !aboutExpanded }) {
                                Icon(
                                    imageVector = if (aboutExpanded)
                                        Icons.Default.ExpandLess
                                    else
                                        Icons.Default.ExpandMore,
                                    contentDescription = if (aboutExpanded) "Collapse" else "Expand"
                                )
                            }
                        }

                        if (aboutExpanded) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Frontier Command Center",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Version 1.0.0",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "A Pioneer-themed Android educational application teaching modern mobile development through 10 progressive camps.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Reset Settings
            item {
                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, "Reset")
                    Spacer(Modifier.width(8.dp))
                    Text("Reset to Defaults")
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Confirmation Dialogs
    if (showClearLogsDialog) {
        ConfirmDialog(
            title = "Clear Logs",
            message = "Are you sure you want to clear all logs? This action cannot be undone.",
            onConfirm = {
                viewModel.clearLogs()
                showClearLogsDialog = false
            },
            onDismiss = { showClearLogsDialog = false }
        )
    }

    if (showClearCacheDialog) {
        ConfirmDialog(
            title = "Clear Cache",
            message = "Are you sure you want to clear all cached data? This will not delete logs or settings.",
            onConfirm = {
                viewModel.clearCache()
                showClearCacheDialog = false
            },
            onDismiss = { showClearCacheDialog = false }
        )
    }

    if (showResetDialog) {
        ConfirmDialog(
            title = "Reset Settings",
            message = "Are you sure you want to reset all settings to defaults?",
            onConfirm = {
                viewModel.resetToDefaults()
                showResetDialog = false
            },
            onDismiss = { showResetDialog = false }
        )
    }
}

/**
 * LogEntryCard - Displays a single log entry
 */
@Composable
fun LogEntryCard(log: com.frontiercommand.model.LogEntry) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = when (log.level) {
            LogLevel.ERROR -> MaterialTheme.colorScheme.errorContainer
            LogLevel.WARNING -> MaterialTheme.colorScheme.tertiaryContainer
            LogLevel.INFO -> MaterialTheme.colorScheme.primaryContainer
            LogLevel.DEBUG -> MaterialTheme.colorScheme.secondaryContainer
        },
        shape = MaterialTheme.shapes.small
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = log.tag,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormat.format(Date(log.timestamp)),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = log.message,
                style = MaterialTheme.typography.bodySmall
            )
            if (log.exception != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Exception: ${log.exception.take(100)}...",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * ConfirmDialog - Reusable confirmation dialog
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
