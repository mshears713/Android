package com.frontiercommand.view.camps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.frontiercommand.repository.LogManager

/**
 * Camp 10: Deployment & Release Preparation
 *
 * **Educational Goals:**
 * - Understand Android app deployment process
 * - Master build variants and configurations
 * - Learn APK/AAB generation and signing
 * - Implement ProGuard/R8 optimization
 * - Prepare app for Google Play Store
 * - Follow release best practices
 *
 * **Key Concepts Covered:**
 * 1. **Build Variants** - Debug vs Release configurations
 * 2. **App Signing** - Keystore creation and management
 * 3. **ProGuard/R8** - Code shrinking and obfuscation
 * 4. **Version Management** - versionCode and versionName
 * 5. **Release Checklist** - Pre-launch verification
 * 6. **Google Play Console** - Publishing workflow
 *
 * **Deployment Process:**
 * 1. Configure build variants
 * 2. Create signing keystore
 * 3. Enable ProGuard/R8
 * 4. Build release APK/AAB
 * 5. Test release build thoroughly
 * 6. Upload to Google Play Console
 * 7. Complete store listing
 * 8. Submit for review
 *
 * This final camp prepares you to deploy your Android apps to
 * production, completing your journey from beginner to ready-to-ship
 * Android developer!
 */
@Composable
fun Camp10Deployment() {
    val context = LocalContext.current
    val logManager = remember { LogManager.getInstance(context) }

    // Track when camp is opened
    LaunchedEffect(Unit) {
        logManager.info("Camp10", "Deployment camp opened")
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
                        text = "🏕️ Camp 10: Deployment",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ship your app to production!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Congratulations
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉 Congratulations, Pioneer! 🎉",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "You've completed all 10 camps of the Frontier Command Center! You've learned modern Android development from the ground up and are now ready to deploy production apps.",
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
                        text = "📚 Deployment Fundamentals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        **Build Variants**

                        Android apps have different build types for development
                        and production:

                        **Debug Build:**
                        • Used during development
                        • Includes debugging symbols
                        • No code optimization
                        • Larger APK size
                        • Can install alongside release build
                        • Signed with debug keystore

                        **Release Build:**
                        • Production-ready version
                        • Code optimized with R8/ProGuard
                        • Smaller APK size
                        • Must be signed with release keystore
                        • Upload to Google Play Store

                        **Build Configuration (build.gradle.kts):**

                        buildTypes {
                            release {
                                isMinifyEnabled = true
                                isShrinkResources = true
                                proguardFiles(
                                    getDefaultProguardFile("proguard-android-optimize.txt"),
                                    "proguard-rules.pro"
                                )
                                signingConfig = signingConfigs.getByName("release")
                            }
                        }

                        **Version Management:**

                        • versionCode: Integer incremented with each release
                        • versionName: User-visible version string (e.g., "1.0.0")

                        defaultConfig {
                            versionCode = 1
                            versionName = "1.0.0"
                        }

                        **App Signing:**

                        All Android apps must be signed. Use different keystores
                        for debug and release:

                        • Debug: Automatic, for development only
                        • Release: You create and manage, for production

                        **NEVER commit your release keystore or passwords to git!**
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Creating Keystore
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔐 Creating a Release Keystore",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Generate a keystore using keytool:",
                        style = MaterialTheme.typography.bodyMedium,
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
                            keytool -genkey -v -keystore my-release-key.jks \
                              -keyalg RSA -keysize 2048 -validity 10000 \
                              -alias my-app-key
                            """.trimIndent(),
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Store keystore credentials securely:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = """
                        1. Create keystore.properties (add to .gitignore!):

                        storePassword=your_store_password
                        keyPassword=your_key_password
                        keyAlias=my-app-key
                        storeFile=../my-release-key.jks

                        2. Load in build.gradle.kts:

                        val keystorePropertiesFile = rootProject.file("keystore.properties")
                        val keystoreProperties = Properties()
                        keystoreProperties.load(FileInputStream(keystorePropertiesFile))

                        3. Configure signing:

                        signingConfigs {
                            create("release") {
                                keyAlias = keystoreProperties["keyAlias"] as String
                                keyPassword = keystoreProperties["keyPassword"] as String
                                storeFile = file(keystoreProperties["storeFile"] as String)
                                storePassword = keystoreProperties["storePassword"] as String
                            }
                        }
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }

        // ProGuard/R8
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔧 ProGuard/R8 Optimization",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        **R8** is Android's code shrinker and obfuscator.
                        It makes your APK smaller and harder to reverse engineer.

                        **What R8 Does:**

                        1. **Code Shrinking** - Removes unused classes and methods
                        2. **Resource Shrinking** - Removes unused resources
                        3. **Obfuscation** - Renames classes/methods to short names
                        4. **Optimization** - Rewrites code for better performance

                        **Enable in build.gradle.kts:**

                        buildTypes {
                            release {
                                isMinifyEnabled = true
                                isShrinkResources = true
                                proguardFiles(
                                    getDefaultProguardFile("proguard-android-optimize.txt"),
                                    "proguard-rules.pro"
                                )
                            }
                        }

                        **ProGuard Rules (proguard-rules.pro):**

                        # Keep model classes for serialization
                        -keep class com.frontiercommand.model.** { *; }

                        # Keep Compose classes
                        -keep class androidx.compose.** { *; }

                        # Keep serialization
                        -keepattributes *Annotation*, InnerClasses
                        -dontnote kotlinx.serialization.**

                        **Common Issues:**

                        • Reflection breaks: Add keep rules
                        • Serialization fails: Keep data classes
                        • Crashes in release: Check stack traces
                        • Missing resources: Verify keep rules
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Building Release APK
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📦 Building Release APK/AAB",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Build signed release APK:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "./gradlew assembleRelease",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Build Android App Bundle (AAB) for Google Play:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "./gradlew bundleRelease",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = """
                        **APK vs AAB:**

                        • APK: Traditional format, larger file
                        • AAB: Google Play format, smaller downloads
                        • AAB recommended for Play Store
                        • APK needed for direct distribution

                        **Output Locations:**

                        • APK: app/build/outputs/apk/release/
                        • AAB: app/build/outputs/bundle/release/

                        **Test Release Build:**

                        adb install app/build/outputs/apk/release/app-release.apk
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Release Checklist
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "✅ Pre-Release Checklist",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ChecklistItem("Update versionCode and versionName")
                    ChecklistItem("Test release build thoroughly")
                    ChecklistItem("Verify ProGuard rules work correctly")
                    ChecklistItem("Test on multiple devices/Android versions")
                    ChecklistItem("Check app permissions are necessary")
                    ChecklistItem("Optimize images and resources")
                    ChecklistItem("Remove debug logs and TODOs")
                    ChecklistItem("Verify app icon and splash screen")
                    ChecklistItem("Test deep links work correctly")
                    ChecklistItem("Prepare store listing assets")
                    ChecklistItem("Write release notes")
                    ChecklistItem("Create privacy policy (if needed)")
                    ChecklistItem("Test offline functionality")
                    ChecklistItem("Verify crash reporting works")
                    ChecklistItem("Back up signing keystore securely")
                }
            }
        }

        // Google Play Console
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🏪 Google Play Console",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        **Publishing Steps:**

                        1. **Create Google Play Developer Account**
                           - One-time ${'$'}25 registration fee
                           - Required for publishing apps

                        2. **Create App in Console**
                           - Choose app name
                           - Select default language
                           - Specify app or game

                        3. **Complete Store Listing**
                           - App description (short & full)
                           - Screenshots (phone, tablet, TV)
                           - App icon (512x512)
                           - Feature graphic (1024x500)
                           - Category and tags

                        4. **Set Up App Content**
                           - Privacy policy URL
                           - Content rating questionnaire
                           - Target audience
                           - Ads declaration

                        5. **Upload Release Bundle**
                           - Upload AAB file
                           - Set release notes
                           - Choose rollout percentage

                        6. **Review and Publish**
                           - Review all sections
                           - Submit for review
                           - Wait for approval (1-3 days)

                        **Release Tracks:**

                        • Internal: Team testing
                        • Closed: Limited testers
                        • Open: Public beta
                        • Production: All users

                        **App Review Process:**

                        Google reviews apps for policy compliance.
                        Address any issues promptly if rejected.
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
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
                        text = "✅ Deployment Best Practices",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        1. **Version Management**
                           - Follow semantic versioning (1.0.0)
                           - Increment versionCode with each release
                           - Update versionName for user visibility

                        2. **Testing**
                           - Test release build, not debug
                           - Test on real devices
                           - Cover all critical user flows
                           - Verify ProGuard doesn't break features

                        3. **Gradual Rollout**
                           - Start with internal testing
                           - Progress to closed testing
                           - Use staged rollout (10%, 25%, 50%, 100%)
                           - Monitor crash reports closely

                        4. **Security**
                           - Never commit keystores to git
                           - Use strong keystore passwords
                           - Back up keystore in multiple locations
                           - Keep passwords in password manager

                        5. **Documentation**
                           - Maintain changelog
                           - Document known issues
                           - Update README with setup instructions
                           - Include troubleshooting guide

                        6. **Post-Launch**
                           - Monitor crash reports
                           - Respond to user reviews
                           - Plan regular updates
                           - Track analytics and metrics
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Journey Complete
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎓 Journey Complete!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = """
                        You've completed all 10 camps of the Frontier Command Center!

                        **What You've Learned:**

                        ✓ MVVM Architecture
                        ✓ Jetpack Compose UI
                        ✓ Navigation Patterns
                        ✓ REST APIs & WebSocket
                        ✓ GPS & Location Services
                        ✓ Data Persistence
                        ✓ State Management
                        ✓ Deep Linking
                        ✓ Background Processing
                        ✓ Notifications
                        ✓ Deployment & Release

                        **You're now ready to:**

                        • Build production Android apps
                        • Implement modern architecture patterns
                        • Handle complex state management
                        • Deploy apps to Google Play Store
                        • Continue learning advanced topics

                        **Keep Exploring:**

                        • Study more Android libraries
                        • Practice building your own apps
                        • Contribute to open source
                        • Share your knowledge with others

                        Welcome to the Android developer community!
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
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
 * ChecklistItem - Displays a checklist item
 */
@Composable
fun ChecklistItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "☑ ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
