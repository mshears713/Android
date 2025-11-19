package com.frontiercommand.viewmodel

import android.app.Application
import com.frontiercommand.model.Command
import com.frontiercommand.repository.NetworkClient
import com.frontiercommand.repository.NetworkException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * CommandViewModel - Manages command execution and history
 *
 * Handles user commands entered in the CommandConsole, processes them,
 * and manages the command history with responses.
 *
 * **Responsibilities:**
 * - Execute commands via NetworkClient (HTTP) or WebSocketClient
 * - Maintain command history as StateFlow
 * - Parse command syntax (GET, POST, SUBSCRIBE, etc.)
 * - Handle errors gracefully with user-friendly messages
 * - Log all command activities
 *
 * **Command Syntax:**
 * ```
 * GET <url>           → HTTP GET request
 * POST <url> <body>   → HTTP POST request
 * HELP                → Show available commands
 * CLEAR               → Clear command history
 * ```
 *
 * **Data Flow:**
 * ```
 * UI input → executeCommand() → NetworkClient → Response → Update StateFlow → UI update
 * ```
 *
 * @param application Application instance for context
 *
 * @see Command for command data structure
 * @see NetworkClient for HTTP operations
 */
class CommandViewModel(application: Application) : BaseViewModel(application) {

    /**
     * Internal mutable state for command history
     */
    private val _commands = MutableStateFlow<List<Command>>(emptyList())

    /**
     * Public read-only state for command history
     * UI collects this to display command console output
     */
    val commands: StateFlow<List<Command>> = _commands.asStateFlow()

    init {
        logInfo("CommandViewModel initialized")
    }

    /**
     * Executes a user command and updates the command history
     *
     * Parses the command text, executes the appropriate action (HTTP request,
     * help display, etc.), and adds the command with response to history.
     *
     * **Supported Commands:**
     * - `GET <url>` - HTTP GET request
     * - `POST <url> <body>` - HTTP POST request
     * - `HELP` - Display available commands
     * - `CLEAR` - Clear command history
     *
     * @param commandText The command string entered by the user
     */
    fun executeCommand(commandText: String) {
        launchSafe {
            try {
                logDebug("Executing command: $commandText")

                // Create command object
                val command = Command(
                    id = UUID.randomUUID().toString(),
                    commandText = commandText,
                    response = null // Will be updated after execution
                )

                // Validate command
                if (!command.isValid()) {
                    addCommandWithResponse(command, "Error: Invalid command format")
                    logWarning("Invalid command: $commandText")
                    return@launchSafe
                }

                // Add command to history (pending)
                _commands.value = _commands.value + command

                // Parse and execute command
                val response = when (command.getVerb()) {
                    "GET" -> executeGetCommand(commandText)
                    "POST" -> executePostCommand(commandText)
                    "HELP" -> getHelpText()
                    "CLEAR" -> {
                        clearCommands()
                        "Command history cleared"
                    }
                    else -> "Error: Unknown command '${command.getVerb()}'. Type HELP for available commands."
                }

                // Update command with response
                updateCommandResponse(command.id, response)
                logInfo("Command executed successfully: ${command.getVerb()}")

            } catch (e: Exception) {
                logError("Error executing command", e)
                // Error is already logged, response already added to history
            }
        }
    }

    /**
     * Executes a GET command
     * Format: GET <url>
     *
     * @param commandText Full command text
     * @return Response from NetworkClient or error message
     */
    private suspend fun executeGetCommand(commandText: String): String {
        return try {
            // Parse URL from command
            // Expected format: "GET /endpoint"
            val parts = commandText.trim().split(" ")
            if (parts.size < 2) {
                return "Error: GET command requires a URL. Usage: GET <url>"
            }

            val url = parts[1]

            // Validate endpoint
            if (!NetworkClient.isValidEndpoint(url)) {
                return "Error: Unknown endpoint '$url'. Try /status, /devices, /logs, or /config"
            }

            // Execute GET request
            val response = NetworkClient.get(url)
            response

        } catch (e: NetworkException) {
            "Network Error: ${e.message}"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    /**
     * Executes a POST command
     * Format: POST <url> <body>
     *
     * @param commandText Full command text
     * @return Response from NetworkClient or error message
     */
    private suspend fun executePostCommand(commandText: String): String {
        return try {
            // Parse URL and body from command
            // Expected format: "POST /endpoint {json body}"
            val parts = commandText.trim().split(" ", limit = 3)
            if (parts.size < 3) {
                return "Error: POST command requires URL and body. Usage: POST <url> <body>"
            }

            val url = parts[1]
            val body = parts[2]

            // Validate endpoint
            if (!NetworkClient.isValidEndpoint(url)) {
                return "Error: Unknown endpoint '$url'. Try /config, /command, or /upload"
            }

            // Execute POST request
            val response = NetworkClient.post(url, body)
            response

        } catch (e: NetworkException) {
            "Network Error: ${e.message}"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    /**
     * Returns help text listing all available commands
     *
     * @return Formatted help text
     */
    private fun getHelpText(): String {
        return """
            Available Commands:

            GET <url>
              Fetch data from an endpoint
              Example: GET /status

            POST <url> <body>
              Send data to an endpoint
              Example: POST /config {"key":"value"}

            HELP
              Display this help message

            CLEAR
              Clear command history

            Available Endpoints:
              GET:  /status, /devices, /logs, /config
              POST: /config, /command, /upload
        """.trimIndent()
    }

    /**
     * Updates a command's response after execution
     *
     * @param commandId The ID of the command to update
     * @param response The response to attach
     */
    private fun updateCommandResponse(commandId: String, response: String) {
        _commands.value = _commands.value.map { command ->
            if (command.id == commandId) {
                command.withResponse(response)
            } else {
                command
            }
        }
    }

    /**
     * Adds a command with an immediate response (for errors)
     *
     * @param command The command to add
     * @param response The response text
     */
    private fun addCommandWithResponse(command: Command, response: String) {
        _commands.value = _commands.value + command.withResponse(response)
    }

    /**
     * Clears all commands from history
     */
    fun clearCommands() {
        launchSafe {
            logInfo("Clearing command history")
            _commands.value = emptyList()
        }
    }

    /**
     * Cleans up when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        logDebug("CommandViewModel cleared")
    }
}
