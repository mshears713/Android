package com.frontiercommand.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.frontiercommand.ui.theme.PioneerTheme
import com.frontiercommand.viewmodel.CampViewModel
import kotlinx.coroutines.delay

/**
 * CampDetailScreen - Detail screen for an individual camp
 *
 * Displays the full content for a specific camp including:
 * - Camp title, description, and module
 * - Completion status with toggle button
 * - Placeholder for future camp-specific content
 * - CommandConsole will be added in Step 13
 *
 * **Data Flow:**
 * ```
 * CampViewModel.getCampById() → Camp → UI display
 * User clicks Complete → CampViewModel.completeCamp() → StateFlow update → UI refresh
 * ```
 *
 * **Features:**
 * - Reactive UI updates on camp completion
 * - Error handling for invalid campId
 * - Auto-return to home on error after delay
 * - Material Design 3 styling with Pioneer theme
 * - Scrollable content for long descriptions
 *
 * **Error Handling:**
 * - Invalid campId shows error message
 * - Auto-navigation back to Home after 3 seconds
 * - Graceful degradation if camp data unavailable
 *
 * @param campId The ID of the camp to display (1-10)
 * @param navController Navigation controller for back navigation
 * @param onNavigateBack Callback invoked when back button is pressed
 * @param modifier Optional modifier for customization
 * @param viewModel The CampViewModel providing camp data
 *
 * @see HomeScreen for the source of navigation
 * @see CampViewModel for data management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampDetailScreen(
    campId: Int,
    navController: NavController,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CampViewModel = viewModel()
) {
    // Get the camp from ViewModel
    val camp = viewModel.getCampById(campId)

    // Handle invalid campId with auto-return to home
    if (camp == null) {
        LaunchedEffect(campId) {
            delay(3000) // Wait 3 seconds before auto-returning
            onNavigateBack()
        }

        // Error state UI
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Error") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Navigate back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        titleContentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Camp not found",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Returning to home...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        return
    }

    // Normal camp detail UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(camp.title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back to home"
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Module badge
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = camp.module,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = camp.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Completion status card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (camp.isCompleted) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (camp.isCompleted) "Completed" else "Not Completed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (camp.isCompleted) {
                                "Great job! You've completed this camp."
                            } else {
                                "Complete the activities below to mark this camp as done."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (camp.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle completion button
            Button(
                onClick = {
                    if (camp.isCompleted) {
                        viewModel.resetCamp(campId)
                    } else {
                        viewModel.completeCamp(campId)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (camp.isCompleted) {
                        "Mark as Incomplete"
                    } else {
                        "Mark as Complete"
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Placeholder for future camp-specific content
            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Camp Content",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Camp-specific interactive content will be added in Phase 2:",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Placeholder content boxes
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "• CommandConsole (Step 13)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Interactive tutorials and demonstrations",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Code examples and explanations",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Schematic diagrams",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Preview for Android Studio design-time rendering
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CampDetailScreenPreview() {
    PioneerTheme {
        Surface {
            CampDetailScreen(
                campId = 1,
                navController = rememberNavController(),
                onNavigateBack = {}
            )
        }
    }
}
