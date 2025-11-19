package com.frontiercommand.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

/**
 * StorageManager - Local JSON file storage manager
 *
 * Provides simple, reliable local data persistence using JSON files.
 * All file operations run on IO dispatcher for non-blocking execution.
 *
 * **Features:**
 * - Save/load JSON data to internal storage
 * - Automatic JSON formatting with pretty print
 * - Comprehensive error handling
 * - Thread-safe with Dispatchers.IO
 * - File listing and deletion
 *
 * **Storage Location:**
 * Files are stored in the app's internal storage directory:
 * `/data/data/com.frontiercommand/files/`
 *
 * These files are:
 * - Private to the app (other apps cannot access)
 * - Automatically deleted when app is uninstalled
 * - Not accessible to users via file manager
 *
 * **Usage:**
 * ```kotlin
 * val storage = StorageManager(context)
 *
 * // Save data
 * val success = storage.saveJson("config.json", """{"key": "value"}""")
 *
 * // Load data
 * val data = storage.loadJson("config.json")
 * if (data != null) {
 *     Log.d("Storage", "Loaded: $data")
 * }
 *
 * // List files
 * val files = storage.listFiles()
 *
 * // Delete file
 * storage.deleteFile("config.json")
 * ```
 *
 * @param context Application or Activity context
 */
class StorageManager(private val context: Context) {

    private val TAG = "StorageManager"

    /**
     * JSON serializer with pretty printing enabled
     */
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * Saves JSON data to a file in internal storage
     *
     * Runs on IO dispatcher to avoid blocking the UI thread.
     * Creates the file if it doesn't exist, overwrites if it does.
     *
     * @param filename Name of the file (e.g., "config.json")
     * @param data JSON string to save
     * @return true if save successful, false otherwise
     */
    suspend fun saveJson(filename: String, data: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Saving JSON to file: $filename")

            // Validate inputs
            if (filename.isBlank()) {
                Log.e(TAG, "Filename cannot be blank")
                return@withContext false
            }

            if (data.isBlank()) {
                Log.w(TAG, "Saving empty data to $filename")
            }

            // Get file in internal storage
            val file = File(context.filesDir, filename)

            // Write data
            file.writeText(data)

            Log.i(TAG, "Successfully saved ${data.length} bytes to $filename")
            true

        } catch (e: IOException) {
            Log.e(TAG, "IO error saving JSON to $filename", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error saving JSON to $filename", e)
            false
        }
    }

    /**
     * Loads JSON data from a file in internal storage
     *
     * Runs on IO dispatcher to avoid blocking the UI thread.
     *
     * @param filename Name of the file to load
     * @return JSON string if successful, null if file doesn't exist or error occurs
     */
    suspend fun loadJson(filename: String): String? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Loading JSON from file: $filename")

            // Validate filename
            if (filename.isBlank()) {
                Log.e(TAG, "Filename cannot be blank")
                return@withContext null
            }

            // Get file
            val file = File(context.filesDir, filename)

            // Check if file exists
            if (!file.exists()) {
                Log.w(TAG, "File does not exist: $filename")
                return@withContext null
            }

            // Read data
            val data = file.readText()
            Log.i(TAG, "Successfully loaded ${data.length} bytes from $filename")
            data

        } catch (e: IOException) {
            Log.e(TAG, "IO error loading JSON from $filename", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error loading JSON from $filename", e)
            null
        }
    }

    /**
     * Checks if a file exists in internal storage
     *
     * @param filename Name of the file to check
     * @return true if file exists, false otherwise
     */
    suspend fun fileExists(filename: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (filename.isBlank()) {
                return@withContext false
            }

            val file = File(context.filesDir, filename)
            file.exists()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking file existence: $filename", e)
            false
        }
    }

    /**
     * Deletes a file from internal storage
     *
     * @param filename Name of the file to delete
     * @return true if deletion successful, false otherwise
     */
    suspend fun deleteFile(filename: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Deleting file: $filename")

            if (filename.isBlank()) {
                Log.e(TAG, "Filename cannot be blank")
                return@withContext false
            }

            val file = File(context.filesDir, filename)

            if (!file.exists()) {
                Log.w(TAG, "Cannot delete non-existent file: $filename")
                return@withContext false
            }

            val deleted = file.delete()
            if (deleted) {
                Log.i(TAG, "Successfully deleted file: $filename")
            } else {
                Log.w(TAG, "Failed to delete file: $filename")
            }

            deleted

        } catch (e: Exception) {
            Log.e(TAG, "Error deleting file: $filename", e)
            false
        }
    }

    /**
     * Lists all files in internal storage
     *
     * @return List of filenames, empty list if error or no files
     */
    suspend fun listFiles(): List<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Listing files in internal storage")

            val filesDir = context.filesDir
            val files = filesDir.listFiles()

            if (files == null || files.isEmpty()) {
                Log.d(TAG, "No files found")
                return@withContext emptyList()
            }

            val fileNames = files.map { it.name }
            Log.i(TAG, "Found ${fileNames.size} files: $fileNames")
            fileNames

        } catch (e: Exception) {
            Log.e(TAG, "Error listing files", e)
            emptyList()
        }
    }

    /**
     * Gets the size of a file in bytes
     *
     * @param filename Name of the file
     * @return File size in bytes, -1 if file doesn't exist or error
     */
    suspend fun getFileSize(filename: String): Long = withContext(Dispatchers.IO) {
        try {
            if (filename.isBlank()) {
                return@withContext -1L
            }

            val file = File(context.filesDir, filename)

            if (!file.exists()) {
                return@withContext -1L
            }

            file.length()

        } catch (e: Exception) {
            Log.e(TAG, "Error getting file size: $filename", e)
            -1L
        }
    }

    /**
     * Saves a serializable object as JSON
     *
     * Uses kotlinx.serialization to convert object to JSON.
     *
     * @param filename Name of the file
     * @param obj Serializable object
     * @return true if save successful, false otherwise
     */
    suspend inline fun <reified T> saveObject(filename: String, obj: T): Boolean {
        return try {
            val jsonString = json.encodeToString(obj)
            saveJson(filename, jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error serializing object to JSON", e)
            false
        }
    }

    /**
     * Loads a serializable object from JSON
     *
     * Uses kotlinx.serialization to parse JSON into object.
     *
     * @param filename Name of the file
     * @return Deserialized object if successful, null otherwise
     */
    suspend inline fun <reified T> loadObject(filename: String): T? {
        return try {
            val jsonString = loadJson(filename) ?: return null
            json.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error deserializing JSON to object", e)
            null
        }
    }

    /**
     * Clears all files from internal storage
     *
     * Use with caution! This deletes all app data files.
     *
     * @return Number of files deleted
     */
    suspend fun clearAllFiles(): Int = withContext(Dispatchers.IO) {
        try {
            Log.w(TAG, "Clearing all files from internal storage")

            val filesDir = context.filesDir
            val files = filesDir.listFiles() ?: return@withContext 0

            var deletedCount = 0
            files.forEach { file ->
                if (file.delete()) {
                    deletedCount++
                }
            }

            Log.i(TAG, "Deleted $deletedCount files")
            deletedCount

        } catch (e: Exception) {
            Log.e(TAG, "Error clearing files", e)
            0
        }
    }
}
