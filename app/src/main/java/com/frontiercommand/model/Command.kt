package com.frontiercommand.model

import kotlinx.serialization.Serializable

/**
 * Command - Represents a user command entered in the CommandConsole
 *
 * Commands simulate interactions with a Raspberry Pi ecosystem, demonstrating
 * the request-response pattern used in command-line interfaces and API interactions.
 *
 * **Command Flow:**
 * 1. User enters commandText in the CommandConsole UI
 * 2. Command is validated (non-empty, proper format)
 * 3. Command is sent to appropriate handler (HTTP, WebSocket, or local)
 * 4. Response is received and stored in the response field
 * 5. CommandConsole displays the updated command with response
 *
 * **Example Commands:**
 * - `GET /status` - Retrieve system status via REST API
 * - `POST /config` - Update configuration
 * - `SUBSCRIBE events` - Subscribe to WebSocket events
 * - `HELP` - Display available commands
 *
 * @property id Unique identifier for this command execution
 * @property commandText The raw command string entered by the user
 * @property response The response received (null if not yet executed or pending)
 * @property timestamp When the command was executed (milliseconds since epoch)
 */
@Serializable
data class Command(
    val id: String,
    val commandText: String,
    val response: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Maximum allowed length for a command to prevent abuse/crashes
     */
    companion object {
        const val MAX_COMMAND_LENGTH = 1000
        const val MIN_COMMAND_LENGTH = 1
    }

    /**
     * Validates that the command is well-formed and executable
     *
     * Checks:
     * - commandText is not blank
     * - commandText is within length limits
     * - id is not empty
     *
     * @return true if the command is valid, false otherwise
     */
    fun isValid(): Boolean {
        val trimmedCommand = commandText.trim()
        return id.isNotBlank() &&
                trimmedCommand.length >= MIN_COMMAND_LENGTH &&
                trimmedCommand.length <= MAX_COMMAND_LENGTH
    }

    /**
     * Returns a copy of this command with a response attached
     *
     * @param responseText The response to attach
     * @return New Command instance with response set
     */
    fun withResponse(responseText: String): Command {
        return copy(response = responseText)
    }

    /**
     * Checks if this command has received a response
     *
     * @return true if response is not null and not empty
     */
    fun hasResponse(): Boolean {
        return !response.isNullOrBlank()
    }

    /**
     * Returns the command text trimmed and ready for execution
     *
     * @return Trimmed command text
     */
    fun getCleanCommandText(): String {
        return commandText.trim()
    }

    /**
     * Extracts the command verb (first word) from the command text
     * e.g., "GET /status" -> "GET"
     *
     * @return The command verb in uppercase, or the full command if no spaces
     */
    fun getVerb(): String {
        return getCleanCommandText().split(" ").firstOrNull()?.uppercase() ?: ""
    }
}
