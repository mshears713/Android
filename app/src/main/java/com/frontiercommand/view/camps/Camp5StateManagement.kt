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
import com.frontiercommand.repository.LogManager
import com.frontiercommand.model.LogLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Camp 5: Advanced State Management Patterns
 *
 * **Educational Goals:**
 * - Understand StateFlow and SharedFlow differences
 * - Learn state combination and transformation patterns
 * - Master derived state and state hoisting
 * - Implement multiple state sources coordination
 * - Handle complex async state updates
 * - Practice proper state lifecycle management
 *
 * **Key Concepts Covered:**
 * 1. **StateFlow** - Hot flow that holds current state and emits to new collectors
 * 2. **SharedFlow** - Hot flow for events without state retention
 * 3. **Derived State** - Computing state from multiple sources
 * 4. **State Combination** - Merging multiple StateFlows
 * 5. **State Transformation** - Mapping and filtering flows
 * 6. **Lifecycle Awareness** - Proper coroutine scope management
 *
 * **Interactive Demos:**
 * - Counter with multiple StateFlows
 * - Event stream with SharedFlow
 * - Combined state from multiple sources
 * - Derived state calculations
 * - Async state updates with delays
 *
 * This camp demonstrates production-ready state management patterns
 * essential for building robust Android applications with Jetpack Compose.
 */
@Composable
fun Camp5StateManagement() {
    val context = LocalContext.current
    val logManager = remember { LogManager.getInstance(context) }
    val scope = rememberCoroutineScope()

    // Track when camp is first displayed
    LaunchedEffect(Unit) {
        logManager.info("Camp5", "State Management camp opened")
    }

    // Demo 1: StateFlow Counter
    val (counter, setCounter) = remember { mutableStateOf(0) }
    val counterFlow = remember { MutableStateFlow(counter) }

    // Update flow when counter changes
    LaunchedEffect(counter) {
        counterFlow.value = counter
    }

    // Demo 2: SharedFlow for events
    val eventFlow = remember { MutableSharedFlow<String>() }
    val events = remember { mutableStateListOf<String>() }

    // Collect events
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            events.add(0, event) // Add to beginning
            if (events.size > 10) {
                events.removeAt(events.size - 1) // Keep only last 10
            }
        }
    }

    // Demo 3: Combined state from multiple sources
    val temperature = remember { MutableStateFlow(20.0) }
    val humidity = remember { MutableStateFlow(50.0) }
    val combinedStatus by remember {
        combine(temperature, humidity) { temp, humid ->
            when {
                temp > 30 && humid > 70 -> "⚠️ Hot & Humid"
                temp > 30 -> "🌡️ Hot"
                temp < 10 -> "❄️ Cold"
                humid > 70 -> "💧 Humid"
                else -> "✅ Comfortable"
            }
        }
    }.collectAsState(initial = "Loading...")

    // Demo 4: Derived state - calculate comfort index
    val comfortIndex by remember {
        combine(temperature, humidity) { temp, humid ->
            // Simplified comfort index calculation
            val index = temp - (humid / 10)
            when {
                index > 25 -> "Poor (${index.toInt()})"
                index > 20 -> "Fair (${index.toInt()})"
                else -> "Good (${index.toInt()})"
            }
        }
    }.collectAsState(initial = "Calculating...")

    // Demo 5: Auto-updating state with async operations
    var autoUpdateEnabled by remember { mutableStateOf(false) }

    // Auto-increment counter when enabled
    LaunchedEffect(autoUpdateEnabled) {
        if (autoUpdateEnabled) {
            logManager.info("Camp5", "Auto-update enabled")
            while (autoUpdateEnabled) {
                delay(1000)
                setCounter(counter + 1)
                scope.launch {
                    eventFlow.emit("Counter incremented to ${counter + 1}")
                }
            }
        } else {
            logManager.info("Camp5", "Auto-update disabled")
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
                        text = "🏕️ Camp 5: State Management",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Master advanced StateFlow patterns for reactive UI",
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
                        text = "📚 State Management Fundamentals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        **StateFlow vs SharedFlow:**

                        • StateFlow: Holds current state, replays to new collectors
                          - Use for: UI state, configuration, data models
                          - Always has a value

                        • SharedFlow: Event stream, no state retention
                          - Use for: One-time events, notifications, navigation
                          - No initial value

                        **Best Practices:**

                        1. Expose StateFlow as read-only from ViewModels
                        2. Use combine() to merge multiple state sources
                        3. Create derived state with map() and filter()
                        4. Manage lifecycle with proper coroutine scopes
                        5. Handle errors in flow collection
                        6. Use stateIn() to convert cold flows to hot
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Demo 1: StateFlow Counter
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Demo 1: StateFlow Counter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "StateFlow maintains current value and emits updates",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Count: $counter",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    setCounter(counter - 1)
                                    scope.launch {
                                        eventFlow.emit("Counter decremented")
                                        logManager.debug("Camp5", "Counter: $counter")
                                    }
                                }
                            ) {
                                Text("-")
                            }

                            Button(
                                onClick = {
                                    setCounter(counter + 1)
                                    scope.launch {
                                        eventFlow.emit("Counter incremented")
                                        logManager.debug("Camp5", "Counter: $counter")
                                    }
                                }
                            ) {
                                Text("+")
                            }

                            Button(
                                onClick = {
                                    setCounter(0)
                                    scope.launch {
                                        eventFlow.emit("Counter reset")
                                        logManager.info("Camp5", "Counter reset to 0")
                                    }
                                }
                            ) {
                                Text("Reset")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = autoUpdateEnabled,
                            onCheckedChange = { autoUpdateEnabled = it }
                        )
                        Text("Auto-increment (1 per second)")
                    }
                }
            }
        }

        // Demo 2: SharedFlow Events
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Demo 2: SharedFlow Event Stream",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "SharedFlow emits events without retaining state",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (events.isEmpty()) {
                        Text(
                            text = "No events yet. Use the counter buttons above!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Recent Events (${events.size}):",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        events.take(5).forEach { event ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "• $event",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                events.clear()
                                scope.launch {
                                    logManager.info("Camp5", "Events cleared")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Clear Events")
                        }
                    }
                }
            }
        }

        // Demo 3: Combined State
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Demo 3: Combined State",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "combine() merges multiple StateFlows into derived state",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Temperature control
                    Text("Temperature: ${temperature.value.toInt()}°C")
                    Slider(
                        value = temperature.value.toFloat(),
                        onValueChange = {
                            scope.launch {
                                temperature.value = it.toDouble()
                                eventFlow.emit("Temperature: ${it.toInt()}°C")
                                logManager.debug("Camp5", "Temperature changed: ${it.toInt()}°C")
                            }
                        },
                        valueRange = 0f..40f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Humidity control
                    Text("Humidity: ${humidity.value.toInt()}%")
                    Slider(
                        value = humidity.value.toFloat(),
                        onValueChange = {
                            scope.launch {
                                humidity.value = it.toDouble()
                                eventFlow.emit("Humidity: ${it.toInt()}%")
                                logManager.debug("Camp5", "Humidity changed: ${it.toInt()}%")
                            }
                        },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Combined status display
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Combined Status:",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = combinedStatus,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Demo 4: Derived State
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Demo 4: Derived State",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Calculate new state from existing state sources",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Comfort Index:",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = comfortIndex,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Calculated from temperature and humidity",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Key Takeaways
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
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
                        ✓ StateFlow holds current state for UI components
                        ✓ SharedFlow emits one-time events without state
                        ✓ combine() merges multiple state sources
                        ✓ Derived state reduces redundant calculations
                        ✓ Proper lifecycle management prevents leaks
                        ✓ collectAsState() bridges Flows and Compose

                        **Next Steps:**
                        - Explore Camp 6 for advanced navigation
                        - Practice state hoisting in your own apps
                        - Learn about hot vs cold flows
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
                            // ViewModel pattern
                            class MyViewModel : ViewModel() {
                                private val _state = MutableStateFlow(0)
                                val state: StateFlow<Int> = _state.asStateFlow()

                                private val _events = MutableSharedFlow<String>()
                                val events: SharedFlow<String> = _events.asSharedFlow()

                                // Derived state
                                val doubled: StateFlow<Int> = state.map { it * 2 }
                                    .stateIn(viewModelScope, SharingStarted.Lazily, 0)

                                fun increment() {
                                    _state.value++
                                    viewModelScope.launch {
                                        _events.emit("Incremented")
                                    }
                                }
                            }

                            // Composable usage
                            @Composable
                            fun MyScreen(viewModel: MyViewModel) {
                                val state by viewModel.state.collectAsState()
                                val doubled by viewModel.doubled.collectAsState()

                                Text("Value: ${'$'}state, Doubled: ${'$'}doubled")
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
