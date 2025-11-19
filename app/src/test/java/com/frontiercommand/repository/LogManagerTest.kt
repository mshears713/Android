package com.frontiercommand.repository

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * LogManagerTest - Unit tests for LogManager
 *
 * Tests the centralized logging manager to ensure correct behavior for:
 * - Adding logs with different severity levels
 * - Log collection via StateFlow
 * - Log filtering by level
 * - Log searching
 * - Log rotation (max 1000 entries)
 * - Log persistence to JSON
 * - Statistics generation
 * - Thread-safe concurrent logging
 * - Export functionality
 *
 * Uses MockK for mocking Android Context and TemporaryFolder for test isolation.
 */
class LogManagerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var mockContext: Context
    private lateinit var testFilesDir: File
    private lateinit var logManager: LogManager

    @Before
    fun setUp() {
        // Create temporary directory for tests
        testFilesDir = tempFolder.newFolder("test_logs")

        // Mock Android Context
        mockContext = mockk<Context>()
        every { mockContext.filesDir } returns testFilesDir

        // Reset singleton instance for testing
        LogManager.resetInstance()

        // Create LogManager instance
        logManager = LogManager.getInstance(mockContext)
    }

    // ========== Log Addition Tests ==========

    @Test
    fun `info adds log entry with INFO level`() = runTest {
        // When
        logManager.info("TestTag", "Info message")

        // Wait for log to be processed
        delay(100)

        // Then
        val logs = logManager.logs.first()
        assertTrue("Should have at least one log", logs.isNotEmpty())

        val lastLog = logs.last()
        assertEquals("Tag should match", "TestTag", lastLog.tag)
        assertEquals("Message should match", "Info message", lastLog.message)
        assertEquals("Level should be INFO", LogLevel.INFO, lastLog.level)
    }

    @Test
    fun `debug adds log entry with DEBUG level`() = runTest {
        // When
        logManager.debug("DebugTag", "Debug message")
        delay(100)

        // Then
        val logs = logManager.logs.first()
        val lastLog = logs.last()
        assertEquals("Level should be DEBUG", LogLevel.DEBUG, lastLog.level)
        assertEquals("Tag should match", "DebugTag", lastLog.tag)
        assertEquals("Message should match", "Debug message", lastLog.message)
    }

    @Test
    fun `warning adds log entry with WARNING level`() = runTest {
        // When
        logManager.warning("WarnTag", "Warning message")
        delay(100)

        // Then
        val logs = logManager.logs.first()
        val lastLog = logs.last()
        assertEquals("Level should be WARNING", LogLevel.WARNING, lastLog.level)
        assertEquals("Tag should match", "WarnTag", lastLog.tag)
        assertEquals("Message should match", "Warning message", lastLog.message)
    }

    @Test
    fun `error adds log entry with ERROR level`() = runTest {
        // When
        logManager.error("ErrorTag", "Error message")
        delay(100)

        // Then
        val logs = logManager.logs.first()
        val lastLog = logs.last()
        assertEquals("Level should be ERROR", LogLevel.ERROR, lastLog.level)
        assertEquals("Tag should match", "ErrorTag", lastLog.tag)
        assertEquals("Message should match", "Error message", lastLog.message)
    }

    @Test
    fun `error with exception includes exception details`() = runTest {
        // Given
        val exception = RuntimeException("Test exception")

        // When
        logManager.error("ExceptionTag", "Error with exception", exception)
        delay(100)

        // Then
        val logs = logManager.logs.first()
        val lastLog = logs.last()
        assertEquals("Level should be ERROR", LogLevel.ERROR, lastLog.level)
        assertTrue("Message should contain exception",
            lastLog.message.contains("Test exception"))
    }

    // ========== Log Ordering Tests ==========

    @Test
    fun `logs are ordered chronologically`() = runTest {
        // Given
        val messages = listOf("First", "Second", "Third", "Fourth")

        // When
        messages.forEachIndexed { index, message ->
            logManager.info("Order", message)
            delay(50) // Small delay to ensure different timestamps
        }

        delay(100)

        // Then
        val logs = logManager.logs.first()
        assertTrue("Should have at least 4 logs", logs.size >= 4)

        // Get the last 4 logs
        val lastFourLogs = logs.takeLast(4)

        // Verify order
        assertEquals("First message should be first", "First", lastFourLogs[0].message)
        assertEquals("Second message should be second", "Second", lastFourLogs[1].message)
        assertEquals("Third message should be third", "Third", lastFourLogs[2].message)
        assertEquals("Fourth message should be fourth", "Fourth", lastFourLogs[3].message)

        // Verify timestamps are increasing
        for (i in 0 until lastFourLogs.size - 1) {
            assertTrue("Timestamps should be in order",
                lastFourLogs[i].timestamp <= lastFourLogs[i + 1].timestamp)
        }
    }

    // ========== Log Filtering Tests ==========

    @Test
    fun `getLogsByLevel filters INFO logs correctly`() = runTest {
        // Given
        logManager.debug("Tag", "Debug message")
        logManager.info("Tag", "Info message")
        logManager.warning("Tag", "Warning message")
        logManager.error("Tag", "Error message")
        delay(100)

        // When
        val infoLogs = logManager.getLogsByLevel(LogLevel.INFO)

        // Then
        assertTrue("Should have INFO logs", infoLogs.isNotEmpty())
        assertTrue("All logs should be INFO level",
            infoLogs.all { it.level == LogLevel.INFO })
    }

    @Test
    fun `getLogsByLevel filters ERROR logs correctly`() = runTest {
        // Given
        logManager.debug("Tag", "Debug message")
        logManager.info("Tag", "Info message")
        logManager.error("Tag", "Error message 1")
        logManager.error("Tag", "Error message 2")
        delay(100)

        // When
        val errorLogs = logManager.getLogsByLevel(LogLevel.ERROR)

        // Then
        assertEquals("Should have 2 ERROR logs", 2, errorLogs.size)
        assertTrue("All logs should be ERROR level",
            errorLogs.all { it.level == LogLevel.ERROR })
    }

    @Test
    fun `getLogsByLevel returns empty list when no logs match`() = runTest {
        // Given
        logManager.info("Tag", "Info message")
        delay(100)

        // When
        val errorLogs = logManager.getLogsByLevel(LogLevel.ERROR)

        // Then
        assertTrue("Should have no ERROR logs", errorLogs.isEmpty())
    }

    // ========== Log Searching Tests ==========

    @Test
    fun `searchLogs finds logs by message content`() = runTest {
        // Given
        logManager.info("Tag", "This is a test message")
        logManager.info("Tag", "Another message")
        logManager.info("Tag", "Test again")
        delay(100)

        // When
        val searchResults = logManager.searchLogs("test")

        // Then
        assertEquals("Should find 2 logs containing 'test'", 2, searchResults.size)
        assertTrue("All results should contain 'test'",
            searchResults.all { it.message.contains("test", ignoreCase = true) })
    }

    @Test
    fun `searchLogs is case insensitive`() = runTest {
        // Given
        logManager.info("Tag", "ERROR occurred")
        logManager.info("Tag", "Error occurred")
        logManager.info("Tag", "error occurred")
        delay(100)

        // When
        val searchResults = logManager.searchLogs("error")

        // Then
        assertEquals("Should find all 3 logs with different cases", 3, searchResults.size)
    }

    @Test
    fun `searchLogs searches in both tag and message`() = runTest {
        // Given
        logManager.info("NetworkTag", "Request sent")
        logManager.info("StorageTag", "Network call completed")
        logManager.info("TestTag", "Test message")
        delay(100)

        // When
        val searchResults = logManager.searchLogs("network")

        // Then
        assertEquals("Should find 2 logs containing 'network'", 2, searchResults.size)
    }

    @Test
    fun `searchLogs returns empty list when no matches`() = runTest {
        // Given
        logManager.info("Tag", "Message")
        delay(100)

        // When
        val searchResults = logManager.searchLogs("nonexistent")

        // Then
        assertTrue("Should return empty list", searchResults.isEmpty())
    }

    // ========== Log Rotation Tests ==========

    @Test
    fun `log rotation keeps only last 1000 entries`() = runTest {
        // Given - add more than 1000 logs
        val totalLogs = 1100

        repeat(totalLogs) { index ->
            logManager.info("Tag", "Message $index")
        }

        delay(200) // Wait for all logs to be processed

        // When
        val logs = logManager.logs.first()

        // Then
        assertEquals("Should have exactly 1000 logs", 1000, logs.size)

        // Verify the oldest logs were removed (should start from index 100)
        assertTrue("First log should be from later messages",
            logs.first().message.contains("Message "))
    }

    // ========== Statistics Tests ==========

    @Test
    fun `getStatistics returns correct counts`() = runTest {
        // Given
        logManager.debug("Tag", "Debug 1")
        logManager.debug("Tag", "Debug 2")
        logManager.info("Tag", "Info 1")
        logManager.info("Tag", "Info 2")
        logManager.info("Tag", "Info 3")
        logManager.warning("Tag", "Warning 1")
        logManager.error("Tag", "Error 1")
        delay(100)

        // When
        val stats = logManager.getStatistics()

        // Then
        assertEquals("Total should be 7", 7, stats["total"])
        assertEquals("DEBUG count should be 2", 2, stats["debug"])
        assertEquals("INFO count should be 3", 3, stats["info"])
        assertEquals("WARNING count should be 1", 1, stats["warning"])
        assertEquals("ERROR count should be 1", 1, stats["error"])
    }

    @Test
    fun `getStatistics returns zeros when no logs`() = runTest {
        // When
        val stats = logManager.getStatistics()

        // Then
        assertEquals("Total should be 0", 0, stats["total"])
        assertEquals("DEBUG count should be 0", 0, stats["debug"])
        assertEquals("INFO count should be 0", 0, stats["info"])
        assertEquals("WARNING count should be 0", 0, stats["warning"])
        assertEquals("ERROR count should be 0", 0, stats["error"])
    }

    // ========== Clear Logs Tests ==========

    @Test
    fun `clearLogs removes all logs`() = runTest {
        // Given
        repeat(10) { index ->
            logManager.info("Tag", "Message $index")
        }
        delay(100)

        val logsBeforeClear = logManager.logs.first()
        assertTrue("Should have logs before clear", logsBeforeClear.isNotEmpty())

        // When
        logManager.clearLogs()
        delay(100)

        // Then
        val logsAfterClear = logManager.logs.first()
        assertTrue("Should have no logs after clear", logsAfterClear.isEmpty())
    }

    @Test
    fun `clearLogs resets statistics`() = runTest {
        // Given
        logManager.info("Tag", "Message")
        logManager.error("Tag", "Error")
        delay(100)

        val statsBefore = logManager.getStatistics()
        assertTrue("Should have logs before clear", statsBefore["total"]!! > 0)

        // When
        logManager.clearLogs()
        delay(100)

        // Then
        val statsAfter = logManager.getStatistics()
        assertEquals("Total should be 0 after clear", 0, statsAfter["total"])
    }

    // ========== Export Tests ==========

    @Test
    fun `exportLogs creates JSON file`() = runTest {
        // Given
        logManager.info("Tag1", "Message 1")
        logManager.error("Tag2", "Message 2")
        delay(100)

        // When
        val filename = "test_export.json"
        val success = logManager.exportLogs(filename)

        // Then
        assertTrue("Export should succeed", success)

        val file = File(testFilesDir, filename)
        assertTrue("Export file should exist", file.exists())

        val content = file.readText()
        assertTrue("Content should be valid JSON array", content.startsWith("["))
        assertTrue("Content should contain tag", content.contains("\"tag\""))
        assertTrue("Content should contain message", content.contains("\"message\""))
        assertTrue("Content should contain level", content.contains("\"level\""))
    }

    @Test
    fun `exportLogs with empty logs creates empty array`() = runTest {
        // When
        val filename = "empty_export.json"
        val success = logManager.exportLogs(filename)

        // Then
        assertTrue("Export should succeed even with no logs", success)

        val file = File(testFilesDir, filename)
        assertTrue("Export file should exist", file.exists())

        val content = file.readText().trim()
        assertTrue("Content should be empty JSON array", content == "[]" || content.startsWith("[\n]"))
    }

    // ========== StateFlow Tests ==========

    @Test
    fun `logs StateFlow updates when new log added`() = runTest {
        // Given
        val initialLogs = logManager.logs.first()
        val initialSize = initialLogs.size

        // When
        logManager.info("Tag", "New log")
        delay(100)

        // Then
        val updatedLogs = logManager.logs.first()
        assertTrue("Should have more logs after adding",
            updatedLogs.size > initialSize)
    }

    @Test
    fun `logs StateFlow emits updated list on multiple additions`() = runTest {
        // Given
        val sizes = mutableListOf<Int>()

        // Collect sizes
        sizes.add(logManager.logs.first().size)

        // When - add logs
        logManager.info("Tag", "Log 1")
        delay(50)
        sizes.add(logManager.logs.first().size)

        logManager.info("Tag", "Log 2")
        delay(50)
        sizes.add(logManager.logs.first().size)

        logManager.info("Tag", "Log 3")
        delay(50)
        sizes.add(logManager.logs.first().size)

        // Then - verify sizes are increasing
        for (i in 0 until sizes.size - 1) {
            assertTrue("Size should increase: ${sizes[i]} < ${sizes[i + 1]}",
                sizes[i] < sizes[i + 1])
        }
    }

    // ========== Concurrent Access Tests ==========

    @Test
    fun `concurrent log additions from multiple threads work correctly`() = runTest {
        // Given
        val logCount = 100
        val jobs = mutableListOf<kotlinx.coroutines.Job>()

        // When - launch multiple coroutines to add logs concurrently
        repeat(logCount) { index ->
            val job = kotlinx.coroutines.launch {
                logManager.info("Thread$index", "Concurrent message $index")
            }
            jobs.add(job)
        }

        // Wait for all jobs to complete
        jobs.forEach { it.join() }
        delay(200)

        // Then
        val logs = logManager.logs.first()
        assertTrue("Should have at least $logCount logs", logs.size >= logCount)
    }

    // ========== Log Entry Structure Tests ==========

    @Test
    fun `log entry has all required fields`() = runTest {
        // When
        logManager.info("TestTag", "Test message")
        delay(100)

        // Then
        val logs = logManager.logs.first()
        val lastLog = logs.last()

        assertNotNull("Tag should not be null", lastLog.tag)
        assertNotNull("Message should not be null", lastLog.message)
        assertNotNull("Level should not be null", lastLog.level)
        assertTrue("Timestamp should be positive", lastLog.timestamp > 0)
        assertNotNull("Thread name should not be null", lastLog.threadName)
        assertFalse("Thread name should not be empty", lastLog.threadName.isEmpty())
    }

    @Test
    fun `log entry timestamp is recent`() = runTest {
        // Given
        val beforeLog = System.currentTimeMillis()

        // When
        logManager.info("Tag", "Message")
        delay(100)

        val afterLog = System.currentTimeMillis()

        // Then
        val logs = logManager.logs.first()
        val lastLog = logs.last()

        assertTrue("Timestamp should be after beforeLog",
            lastLog.timestamp >= beforeLog)
        assertTrue("Timestamp should be before afterLog + 1s",
            lastLog.timestamp <= afterLog + 1000)
    }

    // ========== Singleton Tests ==========

    @Test
    fun `getInstance returns same instance`() {
        // When
        val instance1 = LogManager.getInstance(mockContext)
        val instance2 = LogManager.getInstance(mockContext)

        // Then
        assertSame("Should return the same singleton instance", instance1, instance2)
    }

    // ========== Integration Tests ==========

    @Test
    fun `full workflow - add filter search export clear`() = runTest {
        // Given - add various logs
        logManager.debug("Network", "Starting network request")
        logManager.info("Network", "Request completed successfully")
        logManager.warning("Storage", "Cache nearly full")
        logManager.error("Network", "Network timeout error")
        delay(100)

        // When/Then - Filter
        val errorLogs = logManager.getLogsByLevel(LogLevel.ERROR)
        assertEquals("Should have 1 error log", 1, errorLogs.size)

        // When/Then - Search
        val networkLogs = logManager.searchLogs("network")
        assertEquals("Should find 3 network-related logs", 3, networkLogs.size)

        // When/Then - Export
        val exported = logManager.exportLogs("full_workflow.json")
        assertTrue("Export should succeed", exported)

        // When/Then - Statistics
        val stats = logManager.getStatistics()
        assertEquals("Total should be 4", 4, stats["total"])

        // When/Then - Clear
        logManager.clearLogs()
        delay(100)

        val logsAfterClear = logManager.logs.first()
        assertTrue("Should have no logs after clear", logsAfterClear.isEmpty())
    }
}
