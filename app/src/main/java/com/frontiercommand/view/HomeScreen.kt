package com.frontiercommand.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.frontiercommand.model.Camp
import com.frontiercommand.ui.accessibility.buildContentDescription
import com.frontiercommand.ui.accessibility.heading
import com.frontiercommand.ui.accessibility.liveRegion
import com.frontiercommand.viewmodel.CampViewModel

/**
 * HomeScreen - Main screen displaying the list of all 10 camps
 *
 * This is the landing screen users see when launching the app. It displays
 * a scrollable list of all educational camps with:
 * - Camp titles and descriptions
 * - Module categories
 * - Completion status indicators
 * - Clickable cards for navigation
 *
 * **Data Flow:**
 * ```
 * CampViewModel.camps (StateFlow) → collectAsState() → LazyColumn → CampCard
 * ```
 *
 * **Features:**
 * - Reactive UI updates when camp completion changes
 * - Empty state handling for no camps
 * - Accessible with semantic descriptions
 * - Material Design 3 styling
 * - Scrollable list with efficient lazy rendering
 *
 * **User Interactions:**
 * - Click any camp card to navigate to detail screen
 * - Visual feedback on completion (checkmark icon)
 * - Smooth scrolling for all 10 camps
 *
 * @param navController Navigation controller for screen transitions
 * @param onNavigateToCamp Callback invoked when a camp is clicked
 * @param modifier Optional modifier for customization
 * @param viewModel The CampViewModel providing camp data
 *
 * @see CampDetailScreen for the destination of camp clicks
 * @see CampViewModel for data source
 * @see CampCard for individual camp item rendering
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onNavigateToCamp: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CampViewModel = viewModel()
) {
    // Collect camps state reactively
    // Recompose whenever camps list changes
    val camps by viewModel.camps.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Frontier Command Center",
                        modifier = Modifier.heading(1)
                    )
                },
                actions = {
                    // Help button
                    IconButton(
                        onClick = { navController.navigate("help") },
                        modifier = Modifier.semantics {
                            contentDescription = "Open help and documentation"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = null, // ContentDescription on IconButton instead
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    // Settings button
                    IconButton(
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier.semantics {
                            contentDescription = "Open settings"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null, // ContentDescription on IconButton instead
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
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
        if (camps.isEmpty()) {
            // Empty state - shown if camps fail to load
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .liveRegion(), // Announce empty state to screen readers
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.semantics(mergeDescendants = true) {
                        contentDescription = "No camps available. Check logs for errors."
                    }
                ) {
                    Text(
                        text = "No camps available",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.heading(2)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Check logs for errors",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Camp list - scrollable lazy column
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(camps) { camp ->
                    CampCard(
                        camp = camp,
                        onClick = { onNavigateToCamp(camp.id) }
                    )
                }
            }
        }
    }
}

/**
 * CampCard - Individual camp item in the list
 *
 * Displays a single camp with all its information in a Material Card.
 * Clickable to navigate to the camp's detail screen.
 *
 * **Visual Design:**
 * - Card elevation for depth
 * - Title in bold
 * - Description in secondary text
 * - Module badge
 * - Completion checkmark (if completed)
 *
 * @param camp The camp to display
 * @param onClick Callback when card is clicked
 * @param modifier Optional modifier
 */
@Composable
fun CampCard(
    camp: Camp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completionStatus = if (camp.isCompleted) "completed" else "not completed"
    val cardDescription = buildContentDescription(
        "Camp ${camp.id}",
        camp.title,
        camp.module,
        completionStatus,
        "Tap to open"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics(mergeDescendants = true) {
                contentDescription = cardDescription
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (camp.isCompleted) {
                MaterialTheme.colorScheme.secondaryContainer
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
            // Camp content (left side)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title
                Text(
                    text = camp.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Module badge
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = camp.module,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = camp.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Completion indicator (right side)
            if (camp.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(start = 8.dp)
                )
            }
        }
    }
}

/**
 * Preview for Android Studio design-time rendering
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    Surface {
        HomeScreen(
            navController = rememberNavController(),
            onNavigateToCamp = {}
        )
    }
}

/**
 * Preview for individual camp card
 */
@Preview(showBackground = true)
@Composable
fun CampCardPreview() {
    Surface {
        Column(modifier = Modifier.padding(16.dp)) {
            CampCard(
                camp = Camp(
                    id = 1,
                    title = "Camp 1: REST API Basics",
                    description = "Learn HTTP networking fundamentals with GET and POST requests.",
                    module = "Networking",
                    isCompleted = false
                ),
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            CampCard(
                camp = Camp(
                    id = 2,
                    title = "Camp 2: WebSocket Fundamentals",
                    description = "Explore real-time bidirectional communication.",
                    module = "Real-time Communication",
                    isCompleted = true
                ),
                onClick = {}
            )
        }
    }
}
