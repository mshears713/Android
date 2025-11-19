package com.frontiercommand.view.camps

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.frontiercommand.repository.FrontierNotificationManager
import com.frontiercommand.repository.LogManager

/**
 * Camp 9: System Integration & Notifications
 *
 * **Educational Goals:**
 * - Master Android notification system
 * - Understand notification channels and importance
 * - Implement runtime permission requests
 * - Create rich notifications with actions
 * - Handle notification interactions
 * - Follow Android notification best practices
 *
 * **Key Concepts Covered:**
 * 1. **Notification Channels** - Required for Android 8.0+
 * 2. **Notification Builder** - Creating rich notifications
 * 3. **Runtime Permissions** - POST_NOTIFICATIONS (Android 13+)
 * 4. **Notification Actions** - Interactive buttons
 * 5. **Pending Intents** - Deep linking from notifications
 * 6. **Notification Importance** - Priority levels
 *
 * **Notification Types:**
 * - Status updates (Pi connectivity)
 * - Alerts (critical events)
 * - General information
 * - Notifications with actions
 * - Progress notifications
 *
 * This camp demonstrates production-ready notification patterns
 * essential for building apps that engage users even when
 * the app is not in the foreground.
 */
@Composable
fun Camp9SystemIntegration() {
    val context = LocalContext.current
    val logManager = remember { LogManager.getInstance(context) }
    val notificationManager = remember { FrontierNotificationManager(context) }

    // Permission state
    var hasNotificationPermission by remember {
        mutableStateOf(notificationManager.hasNotificationPermission())
    }

    // Permission launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            logManager.info("Camp9", "Notification permission granted")
        } else {
            logManager.warning("Camp9", "Notification permission denied")
        }
    }

    // Track when camp is opened
    LaunchedEffect(Unit) {
        logManager.info("Camp9", "System Integration camp opened")
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
                        text = "🏕️ Camp 9: System Integration",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Master notifications and system services",
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
                        text = "📚 Notification Fundamentals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        **Why Notifications?**

                        Notifications allow apps to communicate with users even
                        when the app is closed. They're essential for:
                        • Informing users of important events
                        • Prompting user actions
                        • Providing status updates
                        • Re-engaging users

                        **Notification Channels (Android 8.0+)**

                        Channels group notifications by category, allowing users
                        to control settings per channel:
                        • Importance level (sound, vibration, heads-up)
                        • Sound preferences
                        • Badge visibility
                        • Do Not Disturb settings

                        **Channel Importance Levels:**

                        • URGENT: Makes sound and heads-up notification
                        • HIGH: Makes sound
                        • DEFAULT: No sound
                        • LOW: No sound, minimized appearance
                        • MIN: No sound, doesn't appear in status bar

                        **Notification Components:**

                        1. **Small Icon** - Required, monochrome
                        2. **Title** - Brief description
                        3. **Content Text** - Detailed message
                        4. **Actions** - Up to 3 interactive buttons
                        5. **Large Icon** - Optional colored icon/image
                        6. **Big Text/Picture** - Expandable content
                        7. **Priority** - Determines behavior

                        **Runtime Permissions (Android 13+):**

                        POST_NOTIFICATIONS permission is required to show
                        notifications on Android 13 and above. Request at
                        runtime when user opts in to notifications.

                        **Best Practices:**

                        ✓ Use appropriate channel importance
                        ✓ Provide clear, concise messages
                        ✓ Include relevant actions
                        ✓ Handle notification taps with deep links
                        ✓ Respect user preferences
                        ✓ Don't spam notifications
                        ✓ Update/cancel outdated notifications
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Permission Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (hasNotificationPermission)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📱 Notification Permission",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (hasNotificationPermission)
                                    "Granted - You can send notifications"
                                else
                                    "Not granted - Notifications disabled",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            ) {
                                Text("Request")
                            }
                        }
                    }

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Note: Permission not required on Android < 13",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Demo Notifications
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔔 Demo Notifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap buttons below to send different notification types:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Status notification
                    Button(
                        onClick = {
                            notificationManager.showStatusNotification(
                                title = "Pi Connected",
                                message = "Raspberry Pi is online and responding"
                            )
                            logManager.info("Camp9", "Sent status notification")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = hasNotificationPermission
                    ) {
                        Text("Send Status Notification")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Alert notification
                    Button(
                        onClick = {
                            notificationManager.showAlertNotification(
                                title = "Alert: High Temperature",
                                message = "Pi temperature is critically high (85°C). Please check cooling system immediately."
                            )
                            logManager.info("Camp9", "Sent alert notification")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = hasNotificationPermission,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Send Alert Notification")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // General notification
                    Button(
                        onClick = {
                            notificationManager.showGeneralNotification(
                                title = "Data Sync Complete",
                                message = "Successfully synced 42 files to the server"
                            )
                            logManager.info("Camp9", "Sent general notification")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = hasNotificationPermission
                    ) {
                        Text("Send General Notification")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Notification with action
                    Button(
                        onClick = {
                            notificationManager.showNotificationWithAction(
                                title = "Update Available",
                                message = "A new version is ready to install",
                                actionTitle = "Update Now"
                            )
                            logManager.info("Camp9", "Sent notification with action")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = hasNotificationPermission
                    ) {
                        Text("Send Notification with Action")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cancel notifications
                    OutlinedButton(
                        onClick = {
                            notificationManager.cancelAllNotifications()
                            logManager.info("Camp9", "Cancelled all notifications")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel All Notifications")
                    }

                    if (!hasNotificationPermission) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠️ Grant notification permission to test notifications",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Notification Channels
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📋 Notification Channels",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ChannelInfo(
                        name = "Pi Status Updates",
                        description = "Raspberry Pi connectivity and status",
                        importance = "Default"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ChannelInfo(
                        name = "Important Alerts",
                        description = "Critical alerts requiring attention",
                        importance = "High"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ChannelInfo(
                        name = "General Notifications",
                        description = "General information and updates",
                        importance = "Low"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Users can customize channel settings in System Settings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Best Practices
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "✅ Best Practices",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        1. **Create Meaningful Channels**
                           - Group related notifications
                           - Use clear, descriptive names
                           - Set appropriate importance

                        2. **Request Permission Contextually**
                           - Explain why notifications are useful
                           - Request when user opts in
                           - Respect denial gracefully

                        3. **Craft Clear Messages**
                           - Use concise titles
                           - Provide actionable content
                           - Include relevant details

                        4. **Add Appropriate Actions**
                           - Limit to 3 actions max
                           - Use clear action labels
                           - Handle action intents properly

                        5. **Implement Deep Linking**
                           - Navigate to relevant content
                           - Pass necessary arguments
                           - Handle navigation properly

                        6. **Respect User Preferences**
                           - Don't spam notifications
                           - Update/cancel outdated ones
                           - Honor Do Not Disturb

                        7. **Test Thoroughly**
                           - Test all notification types
                           - Verify channel settings
                           - Test permission flow
                           - Check different Android versions
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Key Takeaways
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                        ✓ Notifications keep users engaged
                        ✓ Channels provide user control
                        ✓ Runtime permission required (Android 13+)
                        ✓ Importance determines notification behavior
                        ✓ Actions make notifications interactive
                        ✓ Deep linking provides seamless navigation
                        ✓ Respect user preferences and system settings

                        **Next Steps:**
                        - Explore Camp 10 for deployment
                        - Implement notifications in your apps
                        - Study notification patterns

                        **Congratulations!**
                        You've completed all 9 educational camps!
                        You're now ready to build production Android apps!
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
                            // Create notification channel
                            val channel = NotificationChannel(
                                "status_updates",
                                "Status Updates",
                                NotificationManager.IMPORTANCE_DEFAULT
                            )
                            manager.createNotificationChannel(channel)

                            // Build notification
                            val notification = NotificationCompat.Builder(context, "status_updates")
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle("Pi Connected")
                                .setContentText("Device is online")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true)
                                .build()

                            // Show notification
                            NotificationManagerCompat.from(context)
                                .notify(notificationId, notification)

                            // Request permission (Android 13+)
                            val launcher = rememberLauncherForActivityResult(
                                ActivityResultContracts.RequestPermission()
                            ) { granted ->
                                if (granted) {
                                    // Permission granted
                                }
                            }
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
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

/**
 * ChannelInfo - Displays notification channel information
 */
@Composable
fun ChannelInfo(
    name: String,
    description: String,
    importance: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Importance: $importance",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
