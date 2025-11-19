/**
 * Module-level build configuration for the Frontier Command Center Android app
 *
 * This file configures all dependencies and build settings for the PioneerCamps application:
 * - Jetpack Compose for declarative UI (version 1.5.0+)
 * - Navigation Compose for screen navigation (version 2.7.0+)
 * - Kotlin Coroutines for asynchronous operations (version 1.7.0+)
 * - Lifecycle ViewModel Compose for MVVM architecture
 * - kotlinx.serialization for JSON data handling
 * - Google Play Services Location for GPS integration
 * - Testing frameworks for unit and UI tests
 */

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.frontiercommand"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.frontiercommand"
        minSdk = 21  // Android 5.0 (Lollipop) - widely compatible
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // Java 8 compatibility for modern Kotlin features
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        // Target JVM 1.8 for compatibility with Android
        jvmTarget = "1.8"
    }

    buildFeatures {
        // Enable Jetpack Compose UI toolkit
        compose = true
    }

    composeOptions {
        // Kotlin compiler extension for Compose - must match Kotlin version
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android libraries
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")

    // Jetpack Compose UI toolkit - modern declarative UI framework
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Navigation Compose - screen navigation with type-safe routing
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Lifecycle ViewModel Compose - MVVM architecture integration
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // Kotlin Coroutines - asynchronous programming for networking, I/O, sensors
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // kotlinx.serialization - JSON serialization for data persistence
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Google Play Services Location - GPS and location services
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // WorkManager - background task scheduling and execution
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Testing dependencies
    // JUnit5 - unit testing framework
    testImplementation("junit:junit:4.13.2")

    // AndroidX Test - instrumented testing on Android devices/emulators
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Compose UI Testing - test Compose UI components
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debug dependencies
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
