package com.frontiercommand.repository

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.frontiercommand.MainActivity
import com.frontiercommand.R

/**
 * NotificationManager - Handles all notification operations
 *
 * This class provides a simplified interface for creating and managing
 * Android notifications. It demonstrates:
 * - Notification channels (required for Android 8.0+)
 * - Notification building with actions
 * - Runtime permission handling (Android 13+)
 * - Notification importance levels
 * - Custom notification styles
 *
 * **Notification Channels:**
 * - STATUS: For Pi status updates
 * - ALERTS: For important alerts
 * - GENERAL: For general information
 *
 * **Notification Features:**
 * - Title and message
 * - Icons and images
 * - Actions (buttons)
 * - Tap to open app
 * - Priority levels
 * - Sound and vibration
 *
 * @param context Application context
 */
class FrontierNotificationManager(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val logManager = LogManager.getInstance(context)

    companion object {
        // Notification channels
        const val CHANNEL_STATUS = "pi_status"
        const val CHANNEL_ALERTS = "alerts"
        const val CHANNEL_GENERAL = "general"

        // Notification IDs
        const val NOTIFICATION_ID_STATUS = 1
        const val NOTIFICATION_ID_ALERT = 2
        const val NOTIFICATION_ID_GENERAL = 3
    }

    init {
        createNotificationChannels()
    }

    /**
     * Creates notification channels (required for Android 8.0+)
     *
     * Channels allow users to control notification settings
     * per category (sound, vibration, importance, etc.)
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val statusChannel = NotificationChannel(
                CHANNEL_STATUS,
                "Pi Status Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Raspberry Pi status notifications"
                enableVibration(true)
            }

            val alertsChannel = NotificationChannel(
                CHANNEL_ALERTS,
                "Important Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alerts requiring attention"
                enableVibration(true)
                enableLights(true)
            }

            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "General information and updates"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(statusChannel)
            manager.createNotificationChannel(alertsChannel)
            manager.createNotificationChannel(generalChannel)

            logManager.info("Notifications", "Created notification channels")
        }
    }

    /**
     * Checks if notification permission is granted
     *
     * @return true if permission granted, false otherwise
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Permission not required before Android 13
            true
        }
    }

    /**
     * Shows a Pi status notification
     *
     * @param title Notification title
     * @param message Notification message
     * @param autoCancel Whether notification dismisses on tap
     */
    fun showStatusNotification(
        title: String,
        message: String,
        autoCancel: Boolean = true
    ) {
        if (!hasNotificationPermission()) {
            logManager.warning("Notifications", "Notification permission not granted")
            return
        }

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_STATUS)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(autoCancel)
                .build()

            notificationManager.notify(NOTIFICATION_ID_STATUS, notification)
            logManager.info("Notifications", "Shown status notification: $title")

        } catch (e: Exception) {
            logManager.error("Notifications", "Failed to show status notification", e)
        }
    }

    /**
     * Shows an alert notification
     *
     * @param title Notification title
     * @param message Notification message
     * @param priority Priority level (HIGH by default)
     */
    fun showAlertNotification(
        title: String,
        message: String,
        priority: Int = NotificationCompat.PRIORITY_HIGH
    ) {
        if (!hasNotificationPermission()) {
            logManager.warning("Notifications", "Notification permission not granted")
            return
        }

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .build()

            notificationManager.notify(NOTIFICATION_ID_ALERT, notification)
            logManager.info("Notifications", "Shown alert notification: $title")

        } catch (e: Exception) {
            logManager.error("Notifications", "Failed to show alert notification", e)
        }
    }

    /**
     * Shows a general notification
     *
     * @param title Notification title
     * @param message Notification message
     */
    fun showGeneralNotification(
        title: String,
        message: String
    ) {
        if (!hasNotificationPermission()) {
            logManager.warning("Notifications", "Notification permission not granted")
            return
        }

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_GENERAL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(NOTIFICATION_ID_GENERAL, notification)
            logManager.info("Notifications", "Shown general notification: $title")

        } catch (e: Exception) {
            logManager.error("Notifications", "Failed to show general notification", e)
        }
    }

    /**
     * Shows a notification with action buttons
     *
     * @param title Notification title
     * @param message Notification message
     * @param actionTitle Action button text
     */
    fun showNotificationWithAction(
        title: String,
        message: String,
        actionTitle: String
    ) {
        if (!hasNotificationPermission()) {
            logManager.warning("Notifications", "Notification permission not granted")
            return
        }

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // Action intent
            val actionIntent = Intent(context, MainActivity::class.java)
            val actionPendingIntent = PendingIntent.getActivity(
                context,
                1,
                actionIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_STATUS)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    actionTitle,
                    actionPendingIntent
                )
                .build()

            notificationManager.notify(NOTIFICATION_ID_STATUS, notification)
            logManager.info("Notifications", "Shown notification with action: $title")

        } catch (e: Exception) {
            logManager.error("Notifications", "Failed to show notification with action", e)
        }
    }

    /**
     * Cancels a specific notification
     *
     * @param notificationId The notification ID to cancel
     */
    fun cancelNotification(notificationId: Int) {
        try {
            notificationManager.cancel(notificationId)
            logManager.info("Notifications", "Cancelled notification: $notificationId")
        } catch (e: Exception) {
            logManager.error("Notifications", "Failed to cancel notification", e)
        }
    }

    /**
     * Cancels all notifications
     */
    fun cancelAllNotifications() {
        try {
            notificationManager.cancelAll()
            logManager.info("Notifications", "Cancelled all notifications")
        } catch (e: Exception) {
            logManager.error("Notifications", "Failed to cancel all notifications", e)
        }
    }

    /**
     * Checks if notifications are enabled
     *
     * @return true if notifications enabled, false otherwise
     */
    fun areNotificationsEnabled(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }
}
