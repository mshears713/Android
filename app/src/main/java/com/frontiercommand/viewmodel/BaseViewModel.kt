package com.frontiercommand.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * BaseViewModel - Abstract base class for all ViewModels in the application
 *
 * Provides common functionality for all ViewModels including:
 * - Lifecycle awareness through AndroidViewModel
 * - Centralized error handling with CoroutineExceptionHandler
 * - Logging utilities
 * - Access to application context
 *
 * All ViewModels should extend this class to ensure consistent behavior and error handling.
 *
 * **MVVM Pattern:**
 * - ViewModels survive configuration changes (screen rotations)
 * - viewModelScope automatically cancels when ViewModel is cleared
 * - Never hold references to Views, Activities, or Fragments
 *
 * **Lifecycle:**
 * ```
 * Created → Active → Cleared
 *          ↓
 *    viewModelScope active
 * ```
 *
 * @param application Application instance for accessing app-wide resources
 *
 * @see AndroidViewModel for lifecycle details
 * @see viewModelScope for coroutine scope management
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Tag for logging - defaults to the class simple name
     * Override in subclasses for custom log tags
     */
    protected open val logTag: String = this::class.java.simpleName

    /**
     * Global exception handler for coroutines launched in this ViewModel
     * Catches and logs all uncaught exceptions to prevent crashes
     */
    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        logError("Uncaught exception in coroutine", throwable)
    }

    /**
     * Launch a coroutine in viewModelScope with automatic error handling
     *
     * This is the preferred way to launch coroutines in ViewModels as it:
     * - Uses viewModelScope (auto-cancelled when ViewModel is cleared)
     * - Includes global exception handler
     * - Provides consistent error logging
     *
     * @param block The suspend function to execute
     */
    protected fun launchSafe(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            block()
        }
    }

    /**
     * Log an informational message
     *
     * @param message The message to log
     */
    protected fun logInfo(message: String) {
        Log.i(logTag, message)
    }

    /**
     * Log a warning message
     *
     * @param message The warning message
     */
    protected fun logWarning(message: String) {
        Log.w(logTag, message)
    }

    /**
     * Log an error message with optional exception
     *
     * @param message The error message
     * @param throwable Optional exception to log with stack trace
     */
    protected fun logError(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(logTag, message, throwable)
        } else {
            Log.e(logTag, message)
        }
    }

    /**
     * Log a debug message (only visible in debug builds)
     *
     * @param message The debug message
     */
    protected fun logDebug(message: String) {
        Log.d(logTag, message)
    }

    /**
     * Called when the ViewModel is cleared (lifecycle ends)
     * Override to perform cleanup operations
     */
    override fun onCleared() {
        super.onCleared()
        logDebug("ViewModel cleared - cleaning up resources")
    }
}
