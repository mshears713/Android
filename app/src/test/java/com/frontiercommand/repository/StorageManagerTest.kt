package com.frontiercommand.repository

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * StorageManagerTest - Unit tests for StorageManager
 *
 * Tests the local JSON file storage manager to ensure correct behavior for:
 * - Saving and loading JSON data
 * - File existence checks
 * - File deletion
 * - File listing
 * - File size retrieval
 * - Object serialization and deserialization
 * - Error handling for invalid inputs
 * - Clear all files operation
 *
 * Uses MockK for mocking Android Context and TemporaryFolder for test isolation.
 */
class StorageManagerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var mockContext: Context
    private lateinit var storageManager: StorageManager
    private lateinit var testFilesDir: File

    @Before
    fun setUp() {
        // Create temporary directory for tests
        testFilesDir = tempFolder.newFolder("test_files")

        // Mock Android Context
        mockContext = mockk<Context>()
        every { mockContext.filesDir } returns testFilesDir

        // Create StorageManager instance
        storageManager = StorageManager(mockContext)
    }

    // ========== Save and Load JSON Tests ==========

    @Test
    fun `saveJson creates file and saves data successfully`() = runTest {
        // Given
        val filename = "test_config.json"
        val jsonData = """{"key": "value", "number": 42}"""

        // When
        val result = storageManager.saveJson(filename, jsonData)

        // Then
        assertTrue("saveJson should return true", result)

        val file = File(testFilesDir, filename)
        assertTrue("File should exist", file.exists())
        assertEquals("File content should match", jsonData, file.readText())
    }

    @Test
    fun `loadJson retrieves saved data successfully`() = runTest {
        // Given
        val filename = "test_data.json"
        val jsonData = """{"name": "Test", "value": 123}"""

        // Save data first
        storageManager.saveJson(filename, jsonData)

        // When
        val loadedData = storageManager.loadJson(filename)

        // Then
        assertNotNull("Loaded data should not be null", loadedData)
        assertEquals("Loaded data should match saved data", jsonData, loadedData)
    }

    @Test
    fun `saveJson overwrites existing file`() = runTest {
        // Given
        val filename = "overwrite_test.json"
        val originalData = """{"version": 1}"""
        val newData = """{"version": 2}"""

        // When
        storageManager.saveJson(filename, originalData)
        val firstLoad = storageManager.loadJson(filename)

        storageManager.saveJson(filename, newData)
        val secondLoad = storageManager.loadJson(filename)

        // Then
        assertEquals("First load should match original", originalData, firstLoad)
        assertEquals("Second load should match new data", newData, secondLoad)
    }

    @Test
    fun `loadJson returns null for non-existent file`() = runTest {
        // When
        val result = storageManager.loadJson("non_existent_file.json")

        // Then
        assertNull("Loading non-existent file should return null", result)
    }

    @Test
    fun `saveJson with blank filename returns false`() = runTest {
        // When
        val result = storageManager.saveJson("", """{"data": "test"}""")

        // Then
        assertFalse("Saving with blank filename should return false", result)
    }

    @Test
    fun `loadJson with blank filename returns null`() = runTest {
        // When
        val result = storageManager.loadJson("")

        // Then
        assertNull("Loading with blank filename should return null", result)
    }

    @Test
    fun `saveJson with empty data saves successfully`() = runTest {
        // Given
        val filename = "empty_data.json"

        // When
        val result = storageManager.saveJson(filename, "")

        // Then
        assertTrue("Saving empty data should succeed", result)

        val loaded = storageManager.loadJson(filename)
        assertEquals("Empty data should be loaded as empty string", "", loaded)
    }

    // ========== File Existence Tests ==========

    @Test
    fun `fileExists returns true for existing file`() = runTest {
        // Given
        val filename = "existing_file.json"
        storageManager.saveJson(filename, """{"exists": true}""")

        // When
        val exists = storageManager.fileExists(filename)

        // Then
        assertTrue("File should exist", exists)
    }

    @Test
    fun `fileExists returns false for non-existent file`() = runTest {
        // When
        val exists = storageManager.fileExists("non_existent.json")

        // Then
        assertFalse("File should not exist", exists)
    }

    @Test
    fun `fileExists with blank filename returns false`() = runTest {
        // When
        val exists = storageManager.fileExists("")

        // Then
        assertFalse("Blank filename should return false", exists)
    }

    // ========== File Deletion Tests ==========

    @Test
    fun `deleteFile removes existing file`() = runTest {
        // Given
        val filename = "delete_me.json"
        storageManager.saveJson(filename, """{"delete": true}""")
        assertTrue("File should exist before deletion",
            storageManager.fileExists(filename))

        // When
        val deleted = storageManager.deleteFile(filename)

        // Then
        assertTrue("deleteFile should return true", deleted)
        assertFalse("File should not exist after deletion",
            storageManager.fileExists(filename))
    }

    @Test
    fun `deleteFile returns false for non-existent file`() = runTest {
        // When
        val result = storageManager.deleteFile("non_existent.json")

        // Then
        assertFalse("Deleting non-existent file should return false", result)
    }

    @Test
    fun `deleteFile with blank filename returns false`() = runTest {
        // When
        val result = storageManager.deleteFile("")

        // Then
        assertFalse("Deleting with blank filename should return false", result)
    }

    // ========== File Listing Tests ==========

    @Test
    fun `listFiles returns empty list when no files exist`() = runTest {
        // When
        val files = storageManager.listFiles()

        // Then
        assertNotNull("File list should not be null", files)
        assertTrue("File list should be empty", files.isEmpty())
    }

    @Test
    fun `listFiles returns all saved files`() = runTest {
        // Given
        val filenames = listOf("file1.json", "file2.json", "file3.json")
        filenames.forEach { filename ->
            storageManager.saveJson(filename, """{"file": "$filename"}""")
        }

        // When
        val files = storageManager.listFiles()

        // Then
        assertEquals("Should have 3 files", 3, files.size)
        assertTrue("Should contain file1.json", files.contains("file1.json"))
        assertTrue("Should contain file2.json", files.contains("file2.json"))
        assertTrue("Should contain file3.json", files.contains("file3.json"))
    }

    @Test
    fun `listFiles excludes deleted files`() = runTest {
        // Given
        storageManager.saveJson("keep.json", """{"keep": true}""")
        storageManager.saveJson("delete.json", """{"delete": true}""")

        // Delete one file
        storageManager.deleteFile("delete.json")

        // When
        val files = storageManager.listFiles()

        // Then
        assertEquals("Should have 1 file", 1, files.size)
        assertTrue("Should contain keep.json", files.contains("keep.json"))
        assertFalse("Should not contain delete.json", files.contains("delete.json"))
    }

    // ========== File Size Tests ==========

    @Test
    fun `getFileSize returns correct size for existing file`() = runTest {
        // Given
        val filename = "sized_file.json"
        val data = """{"test": "data"}"""
        storageManager.saveJson(filename, data)

        // When
        val size = storageManager.getFileSize(filename)

        // Then
        assertTrue("File size should be positive", size > 0)
        assertEquals("File size should match data length", data.length.toLong(), size)
    }

    @Test
    fun `getFileSize returns -1 for non-existent file`() = runTest {
        // When
        val size = storageManager.getFileSize("non_existent.json")

        // Then
        assertEquals("Non-existent file should return -1", -1L, size)
    }

    @Test
    fun `getFileSize with blank filename returns -1`() = runTest {
        // When
        val size = storageManager.getFileSize("")

        // Then
        assertEquals("Blank filename should return -1", -1L, size)
    }

    @Test
    fun `getFileSize returns 0 for empty file`() = runTest {
        // Given
        val filename = "empty.json"
        storageManager.saveJson(filename, "")

        // When
        val size = storageManager.getFileSize(filename)

        // Then
        assertEquals("Empty file should have size 0", 0L, size)
    }

    // ========== Object Serialization Tests ==========

    @Serializable
    data class TestData(
        val id: Int,
        val name: String,
        val active: Boolean
    )

    @Test
    fun `saveObject serializes and saves object successfully`() = runTest {
        // Given
        val filename = "test_object.json"
        val testObject = TestData(id = 1, name = "Test", active = true)

        // When
        val result = storageManager.saveObject(filename, testObject)

        // Then
        assertTrue("saveObject should return true", result)

        val file = File(testFilesDir, filename)
        assertTrue("File should exist", file.exists())

        val content = file.readText()
        assertTrue("Content should contain id", content.contains("\"id\""))
        assertTrue("Content should contain name", content.contains("\"name\""))
        assertTrue("Content should contain active", content.contains("\"active\""))
    }

    @Test
    fun `loadObject deserializes object successfully`() = runTest {
        // Given
        val filename = "load_object.json"
        val originalObject = TestData(id = 42, name = "Original", active = false)

        // Save object
        storageManager.saveObject(filename, originalObject)

        // When
        val loadedObject = storageManager.loadObject<TestData>(filename)

        // Then
        assertNotNull("Loaded object should not be null", loadedObject)
        assertEquals("ID should match", originalObject.id, loadedObject?.id)
        assertEquals("Name should match", originalObject.name, loadedObject?.name)
        assertEquals("Active should match", originalObject.active, loadedObject?.active)
    }

    @Test
    fun `loadObject returns null for non-existent file`() = runTest {
        // When
        val result = storageManager.loadObject<TestData>("non_existent.json")

        // Then
        assertNull("Loading non-existent object should return null", result)
    }

    @Test
    fun `saveObject and loadObject handle complex objects`() = runTest {
        // Given
        @Serializable
        data class ComplexData(
            val numbers: List<Int>,
            val nested: Map<String, String>,
            val flag: Boolean
        )

        val filename = "complex_object.json"
        val complexObject = ComplexData(
            numbers = listOf(1, 2, 3, 4, 5),
            nested = mapOf("key1" to "value1", "key2" to "value2"),
            flag = true
        )

        // When
        val saved = storageManager.saveObject(filename, complexObject)
        val loaded = storageManager.loadObject<ComplexData>(filename)

        // Then
        assertTrue("Save should succeed", saved)
        assertNotNull("Loaded object should not be null", loaded)
        assertEquals("Numbers should match", complexObject.numbers, loaded?.numbers)
        assertEquals("Nested map should match", complexObject.nested, loaded?.nested)
        assertEquals("Flag should match", complexObject.flag, loaded?.flag)
    }

    // ========== Clear All Files Tests ==========

    @Test
    fun `clearAllFiles removes all files`() = runTest {
        // Given
        val filenames = listOf("file1.json", "file2.json", "file3.json", "file4.json")
        filenames.forEach { filename ->
            storageManager.saveJson(filename, """{"file": "$filename"}""")
        }

        // Verify files exist
        assertEquals("Should have 4 files", 4, storageManager.listFiles().size)

        // When
        val deletedCount = storageManager.clearAllFiles()

        // Then
        assertEquals("Should delete 4 files", 4, deletedCount)
        assertTrue("File list should be empty after clear",
            storageManager.listFiles().isEmpty())
    }

    @Test
    fun `clearAllFiles returns 0 when no files exist`() = runTest {
        // When
        val deletedCount = storageManager.clearAllFiles()

        // Then
        assertEquals("Should delete 0 files", 0, deletedCount)
    }

    // ========== Integration Tests ==========

    @Test
    fun `save load delete workflow works correctly`() = runTest {
        // Given
        val filename = "workflow_test.json"
        val data = """{"workflow": "test"}"""

        // When/Then - Save
        val saved = storageManager.saveJson(filename, data)
        assertTrue("Save should succeed", saved)
        assertTrue("File should exist", storageManager.fileExists(filename))

        // When/Then - Load
        val loaded = storageManager.loadJson(filename)
        assertEquals("Loaded data should match", data, loaded)

        // When/Then - Delete
        val deleted = storageManager.deleteFile(filename)
        assertTrue("Delete should succeed", deleted)
        assertFalse("File should not exist", storageManager.fileExists(filename))

        // When/Then - Load after delete
        val loadedAfterDelete = storageManager.loadJson(filename)
        assertNull("Load after delete should return null", loadedAfterDelete)
    }

    @Test
    fun `multiple concurrent saves and loads work correctly`() = runTest {
        // Given
        val files = (1..10).map { "concurrent_$it.json" to """{"index": $it}""" }

        // When - Save all files
        files.forEach { (filename, data) ->
            storageManager.saveJson(filename, data)
        }

        // Then - Verify all files exist and can be loaded
        val fileList = storageManager.listFiles()
        assertEquals("Should have 10 files", 10, fileList.size)

        files.forEach { (filename, expectedData) ->
            val loaded = storageManager.loadJson(filename)
            assertEquals("Data should match for $filename", expectedData, loaded)
        }
    }

    @Test
    fun `large JSON data saves and loads correctly`() = runTest {
        // Given
        val filename = "large_data.json"
        val largeData = buildString {
            append("{\"items\": [")
            repeat(1000) { index ->
                append("""{"id": $index, "name": "Item $index"}""")
                if (index < 999) append(",")
            }
            append("]}")
        }

        // When
        val saved = storageManager.saveJson(filename, largeData)
        val loaded = storageManager.loadJson(filename)

        // Then
        assertTrue("Save should succeed", saved)
        assertNotNull("Loaded data should not be null", loaded)
        assertEquals("Data length should match", largeData.length, loaded?.length)
    }
}
