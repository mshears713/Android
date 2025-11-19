package com.frontiercommand.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontiercommand.model.Command
import com.frontiercommand.ui.theme.PioneerTheme
import kotlinx.coroutines.launch

/**
 * CommandConsole - Interactive command-line interface UI component
 *
 * A terminal-style console for entering and executing commands. Simulates a
 * command-line interface with input field, send button, and scrollable output history.
 *
 * **Features:**
 * - Text input with validation (max 1000 characters)
 * - Disabled send button for invalid inputs (empty, whitespace-only, too long)
 * - Scrollable command history with responses
 * - Monospace font for terminal aesthetic
 * - Auto-scroll to bottom on new commands
 * - Clear visual distinction between commands and responses
 *
 * **Usage:**
 * ```kotlin
 * CommandConsole(
 *     commands = commandList,
 *     onSendCommand = { commandText ->
 *         viewModel.executeCommand(commandText)
 *     }
 * )
 * ```
 *
 * **Command Flow:**
 * 1. User types command in input field
 * 2. Input is validated (trimmed, length checked)
 * 3. Send button enabled if valid
 * 4. User clicks send → onSendCommand callback invoked
 * 5. ViewModel processes command and updates commands list
 * 6. Console displays updated command with response
 *
 * @param commands List of commands to display (from ViewModel StateFlow)
 * @param onSendCommand Callback invoked when user sends a command
 * @param modifier Optional modifier for customization
 *
 * @see Command for command data structure
 */
@Composable
fun CommandConsole(
    commands: List<Command>,
    onSendCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Input state
    var inputText by remember { mutableStateOf("") }

    // Scroll state for auto-scrolling
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when commands change
    LaunchedEffect(commands.size) {
        if (commands.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(commands.size - 1)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        // Console title
        Text(
            text = "Command Console",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Divider()

        Spacer(modifier = Modifier.height(8.dp))

        // Command output area (scrollable history)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E) // Dark terminal background
            )
        ) {
            if (commands.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No commands yet. Type a command below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF888888), // Gray text
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                // Command history
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(commands) { command ->
                        CommandItem(command = command)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Input field
            OutlinedTextField(
                value = inputText,
                onValueChange = { newValue ->
                    // Limit input to MAX_COMMAND_LENGTH
                    if (newValue.length <= Command.MAX_COMMAND_LENGTH) {
                        inputText = newValue
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Enter command (e.g., GET /status)...",
                        fontFamily = FontFamily.Monospace
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Monospace
                )
            )

            // Send button
            val isInputValid = inputText.trim().length >= Command.MIN_COMMAND_LENGTH

            IconButton(
                onClick = {
                    val trimmedCommand = inputText.trim()
                    if (trimmedCommand.isNotEmpty()) {
                        onSendCommand(trimmedCommand)
                        inputText = "" // Clear input after sending
                    }
                },
                enabled = isInputValid,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isInputValid) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = if (isInputValid) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = if (isInputValid) "Send command" else "Command is empty"
                )
            }
        }

        // Character counter
        Text(
            text = "${inputText.length}/${Command.MAX_COMMAND_LENGTH}",
            style = MaterialTheme.typography.labelSmall,
            color = if (inputText.length > Command.MAX_COMMAND_LENGTH * 0.9) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
        )
    }
}

/**
 * CommandItem - Individual command display in the console
 *
 * Shows a command and its response (if available) with terminal styling.
 *
 * **Visual Style:**
 * - Command text in green (like bash prompt)
 * - Response text in white/gray
 * - Monospace font for terminal look
 * - Clear separation between command and response
 *
 * @param command The command to display
 */
@Composable
fun CommandItem(
    command: Command,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Command text (what user typed)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = ">",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00FF00) // Green prompt like bash
            )
            Text(
                text = command.commandText,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF00FF00) // Green command text
            )
        }

        // Response (if available)
        if (command.hasResponse()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = command.response ?: "",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFCCCCCC), // Light gray response
                modifier = Modifier.padding(start = 12.dp)
            )
        } else {
            // Pending indicator
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Executing...",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFFFAA00), // Orange for pending
                fontWeight = FontWeight.Italic,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

/**
 * Preview for Android Studio design-time rendering
 */
@Preview(showBackground = true)
@Composable
fun CommandConsolePreview() {
    PioneerTheme {
        Surface {
            val sampleCommands = listOf(
                Command(
                    id = "1",
                    commandText = "GET /status",
                    response = """{"status": "online", "uptime": 3600}"""
                ),
                Command(
                    id = "2",
                    commandText = "GET /devices",
                    response = """{"devices": ["pi-001", "pi-002"]}"""
                ),
                Command(
                    id = "3",
                    commandText = "POST /command",
                    response = null // Pending
                )
            )

            CommandConsole(
                commands = sampleCommands,
                onSendCommand = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp)
            )
        }
    }
}

/**
 * Preview of empty console
 */
@Preview(showBackground = true)
@Composable
fun CommandConsoleEmptyPreview() {
    PioneerTheme {
        Surface {
            CommandConsole(
                commands = emptyList(),
                onSendCommand = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp)
            )
        }
    }
}
