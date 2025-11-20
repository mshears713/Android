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
import androidx.navigation.NavController
import com.frontiercommand.repository.LogManager

/**
 * HelpScreen - Application help, tutorials, and documentation
 *
 * Provides comprehensive help content including:
 * - Getting started guide
 * - Camp-by-camp tutorials
 * - Architecture diagrams and explanations
 * - Troubleshooting guide
 * - FAQ section
 * - About the app
 *
 * **Educational Content:**
 * - MVVM architecture explanation
 * - Jetpack Compose fundamentals
 * - Navigation patterns
 * - State management concepts
 * - Data persistence strategies
 *
 * This screen serves as the central documentation hub for the
 * Frontier Command Center educational experience.
 *
 * @param navController Navigation controller for back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    val context = LocalContext.current
    val logManager = remember { LogManager.getInstance(context) }

    // Track screen open
    LaunchedEffect(Unit) {
        logManager.info("Help", "Help screen opened")
    }

    // Expanded sections state
    var gettingStartedExpanded by remember { mutableStateOf(true) }
    var campsExpanded by remember { mutableStateOf(false) }
    var architectureExpanded by remember { mutableStateOf(false) }
    var troubleshootingExpanded by remember { mutableStateOf(false) }
    var faqExpanded by remember { mutableStateOf(false) }

    // Help sections data
    val campGuides = remember {
        listOf(
            CampGuide(
                number = 1,
                title = "REST API Basics",
                description = "Learn HTTP networking with GET and POST requests",
                topics = listOf(
                    "Understanding HTTP methods",
                    "Request and response cycle",
                    "Placeholder NetworkClient usage",
                    "Simulating API calls"
                )
            ),
            CampGuide(
                number = 2,
                title = "WebSocket Fundamentals",
                description = "Real-time bidirectional communication",
                topics = listOf(
                    "WebSocket connection lifecycle",
                    "Sending and receiving messages",
                    "Connection status monitoring",
                    "Error handling"
                )
            ),
            CampGuide(
                number = 3,
                title = "GPS Integration",
                description = "Device location services and permissions",
                topics = listOf(
                    "Runtime permission requests",
                    "FusedLocationProviderClient usage",
                    "Handling location updates",
                    "GPS unavailable scenarios"
                )
            ),
            CampGuide(
                number = 4,
                title = "Offline Data Caching",
                description = "Local JSON file storage",
                topics = listOf(
                    "StorageManager API",
                    "Saving and loading JSON",
                    "Cache management",
                    "Error handling"
                )
            ),
            CampGuide(
                number = 5,
                title = "State Management",
                description = "Advanced StateFlow patterns",
                topics = listOf(
                    "StateFlow vs SharedFlow",
                    "State combination",
                    "Derived state",
                    "Reactive UI updates"
                )
            ),
            CampGuide(
                number = 6,
                title = "Advanced Navigation",
                description = "Deep linking and navigation patterns",
                topics = listOf(
                    "Deep link configuration",
                    "Custom URI schemes",
                    "App Links verification",
                    "Backstack management"
                )
            ),
            CampGuide(
                number = 7,
                title = "Data Persistence",
                description = "Extended caching strategies",
                topics = listOf(
                    "Offline-first architecture",
                    "Caching patterns",
                    "Data synchronization",
                    "Conflict resolution"
                )
            ),
            CampGuide(
                number = 8,
                title = "Background Processing",
                description = "WorkManager for background tasks",
                topics = listOf(
                    "WorkManager setup",
                    "Scheduling work",
                    "Constraints and policies",
                    "Monitoring work status"
                )
            ),
            CampGuide(
                number = 9,
                title = "System Integration",
                description = "Notifications and system services",
                topics = listOf(
                    "Notification channels",
                    "Creating notifications",
                    "Notification actions",
                    "System service integration"
                )
            ),
            CampGuide(
                number = 10,
                title = "Deployment",
                description = "Build variants and release preparation",
                topics = listOf(
                    "Build configurations",
                    "Release signing",
                    "ProGuard/R8 optimization",
                    "APK generation"
                )
            )
        )
    }

    val faqItems = remember {
        listOf(
            FAQItem(
                question = "What is the Frontier Command Center?",
                answer = "A Pioneer-themed Android educational app that teaches modern mobile development through 10 progressive camps, each focusing on a specific Android development concept."
            ),
            FAQItem(
                question = "What will I learn?",
                answer = "You'll master MVVM architecture, Jetpack Compose, navigation, networking, GPS integration, data persistence, state management, background processing, and deployment."
            ),
            FAQItem(
                question = "Do I need a Raspberry Pi?",
                answer = "No! The app uses placeholder networking clients that simulate Raspberry Pi interactions without requiring actual hardware."
            ),
            FAQItem(
                question = "How do I navigate between camps?",
                answer = "Tap any camp from the home screen to view its content. Use the back button to return to the camp list."
            ),
            FAQItem(
                question = "What are deep links?",
                answer = "Deep links allow you to navigate directly to specific camps using URLs like frontiercommand://camp/1. Learn more in Camp 6!"
            ),
            FAQItem(
                question = "How do I view application logs?",
                answer = "Navigate to Settings (accessible from the home screen) to view, filter, and export application logs."
            ),
            FAQItem(
                question = "Can I reset my progress?",
                answer = "Yes! In Settings, you can clear all data including cache, logs, and progress. You can also reset individual camps."
            ),
            FAQItem(
                question = "What is MVVM?",
                answer = "Model-View-ViewModel is an architectural pattern that separates UI (View) from business logic (ViewModel) and data (Model), making apps more testable and maintainable."
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Documentation") },
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
            // Welcome Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🏕️ Welcome to Frontier Command Center",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your journey through modern Android development begins here!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Getting Started Section
            item {
                ExpandableHelpSection(
                    title = "🚀 Getting Started",
                    icon = Icons.Default.PlayArrow,
                    expanded = gettingStartedExpanded,
                    onToggle = { gettingStartedExpanded = !gettingStartedExpanded }
                ) {
                    Text(
                        text = """
                        **Welcome, Pioneer!**

                        The Frontier Command Center is designed to teach you Android development through hands-on experience. Each "Camp" represents a core Android concept.

                        **How to Use This App:**

                        1. **Explore Camps**: From the home screen, browse the 10 available camps
                        2. **Learn Concepts**: Each camp includes interactive demos and educational content
                        3. **Try Commands**: Use the Command Console to execute simulated operations
                        4. **Track Progress**: Mark camps as complete as you learn
                        5. **Check Settings**: View logs, manage cache, and configure preferences
                        6. **Get Help**: Return here anytime for guidance

                        **Recommended Path:**

                        Work through camps in order (1-10) as each builds on previous concepts. Take your time with interactive demos and read all educational content.

                        **Tips for Success:**

                        • Experiment with the Command Console
                        • Review code examples in each camp
                        • Check Settings logs to see app behavior
                        • Test deep links from Camp 6
                        • Try offline features from Camp 7

                        Ready to start? Head back to the home screen and dive into Camp 1!
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Camps Overview Section
            item {
                ExpandableHelpSection(
                    title = "🏕️ Camps Overview (${campGuides.size})",
                    icon = Icons.Default.School,
                    expanded = campsExpanded,
                    onToggle = { campsExpanded = !campsExpanded }
                ) {
                    campGuides.forEach { guide ->
                        CampGuideCard(guide)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // Architecture Section
            item {
                ExpandableHelpSection(
                    title = "🏗️ Architecture Guide",
                    icon = Icons.Default.Architecture,
                    expanded = architectureExpanded,
                    onToggle = { architectureExpanded = !architectureExpanded }
                ) {
                    Text(
                        text = """
                        **MVVM Architecture**

                        The app follows the Model-View-ViewModel pattern:

                        **Model (Data Layer)**
                        • Camp, Command, LogEntry data classes
                        • Serializable with kotlinx.serialization
                        • Immutable for thread safety

                        **View (UI Layer)**
                        • Jetpack Compose declarative UI
                        • Reactive to state changes
                        • No business logic
                        • Screens: HomeScreen, CampDetailScreen, SettingsScreen, etc.

                        **ViewModel (Logic Layer)**
                        • CampViewModel, SettingsViewModel
                        • Exposes StateFlow for reactive data
                        • Handles business logic
                        • Lifecycle-aware with viewModelScope

                        **Repository Pattern**
                        • NetworkClient - HTTP operations
                        • WebSocketClient - Real-time messaging
                        • StorageManager - File I/O
                        • SensorManager - GPS access
                        • LogManager - Centralized logging

                        **Navigation**
                        • Navigation Compose with NavHost
                        • Type-safe route arguments
                        • Deep linking support
                        • Screen sealed class for routes

                        **State Management**
                        • StateFlow for UI state
                        • SharedFlow for one-time events
                        • Coroutines for async operations
                        • Dispatchers.IO for background work

                        **Data Flow:**
                        1. User interacts with View (Composable)
                        2. View calls ViewModel function
                        3. ViewModel updates state or calls Repository
                        4. Repository performs operation (network, storage, sensor)
                        5. State updates trigger UI recomposition
                        6. View reflects new state automatically

                        This architecture ensures:
                        • Separation of concerns
                        • Testability
                        • Maintainability
                        • Scalability
                        • Production-ready patterns
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Troubleshooting Section
            item {
                ExpandableHelpSection(
                    title = "🔧 Troubleshooting",
                    icon = Icons.Default.Build,
                    expanded = troubleshootingExpanded,
                    onToggle = { troubleshootingExpanded = !troubleshootingExpanded }
                ) {
                    Text(
                        text = """
                        **Common Issues and Solutions:**

                        **GPS not working**
                        • Grant location permissions when prompted
                        • Enable GPS in device settings
                        • Try outdoors for better signal
                        • Check Settings logs for errors

                        **Commands not responding**
                        • Ensure command syntax is correct
                        • Try HELP command to see available commands
                        • Check network simulation is enabled
                        • Review logs in Settings

                        **Cache not saving**
                        • Check storage permissions
                        • Verify sufficient storage space
                        • Clear cache and try again
                        • Check Settings logs for I/O errors

                        **Deep links not working**
                        • Ensure correct URI format
                        • Check AndroidManifest configuration
                        • Test with ADB commands (see Camp 6)
                        • Verify app link verification status

                        **App crashes or freezes**
                        • Check Settings logs for exceptions
                        • Clear all cache in Settings
                        • Reset app to defaults
                        • Restart the application

                        **Settings not persisting**
                        • Ensure app has storage permissions
                        • Check for file I/O errors in logs
                        • Try clearing app data and reconfiguring
                        • Verify StorageManager is working

                        **Can't find specific camp**
                        • All camps are on the home screen
                        • Scroll through the camp list
                        • Use deep link: frontiercommand://camp/[number]
                        • Check camp routing in navigation

                        Still having issues? Check the logs in Settings for detailed error messages and stack traces.
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // FAQ Section
            item {
                ExpandableHelpSection(
                    title = "❓ FAQ (${faqItems.size})",
                    icon = Icons.Default.Help,
                    expanded = faqExpanded,
                    onToggle = { faqExpanded = !faqExpanded }
                ) {
                    faqItems.forEach { faq ->
                        FAQCard(faq)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // About Section
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
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

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Frontier Command Center",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = """
                            A Pioneer-themed Android educational application designed to teach modern mobile development through 10 progressive camps.

                            **Built with:**
                            • Kotlin 1.7+
                            • Jetpack Compose 1.3+
                            • Material Design 3
                            • MVVM Architecture
                            • Coroutines & Flows
                            • Navigation Compose

                            **Target Audience:**
                            Entry-level Android developers and beginners eager to learn mobile app architecture and engineering best practices.

                            **Educational Goals:**
                            Master MVVM, Compose UI, navigation, networking, GPS integration, data persistence, state management, background processing, and deployment.

                            © 2024 Frontier Command Center
                            Built for educational purposes.
                            """.trimIndent(),
                            style = MaterialTheme.typography.bodySmall
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
}

/**
 * ExpandableHelpSection - Collapsible help section
 */
@Composable
fun ExpandableHelpSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}

/**
 * CampGuideCard - Displays a camp guide
 */
@Composable
fun CampGuideCard(guide: CampGuide) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Camp ${guide.number}: ${guide.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = guide.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Topics covered:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            guide.topics.forEach { topic ->
                Text(
                    text = "• $topic",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                )
            }
        }
    }
}

/**
 * FAQCard - Displays an FAQ item
 */
@Composable
fun FAQCard(faq: FAQItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Q: ${faq.question}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A: ${faq.answer}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * CampGuide - Data class for camp guide information
 */
data class CampGuide(
    val number: Int,
    val title: String,
    val description: String,
    val topics: List<String>
)

/**
 * FAQItem - Data class for FAQ entries
 */
data class FAQItem(
    val question: String,
    val answer: String
)
