#!/bin/bash

echo "🚀 Frontier Command Center - Installation Helper"
echo "================================================"
echo ""

# Check if ADB is available
if ! command -v adb &> /dev/null; then
    echo "❌ ADB not found. Please install Android SDK Platform Tools."
    echo "   Download: https://developer.android.com/studio/releases/platform-tools"
    echo ""
    echo "   Or install via package manager:"
    echo "   • Ubuntu/Debian: sudo apt install adb"
    echo "   • macOS: brew install android-platform-tools"
    exit 1
fi

# Check for connected devices
echo "📱 Checking for connected devices..."
DEVICES=$(adb devices | grep -w "device" | wc -l)

if [ $DEVICES -eq 0 ]; then
    echo "❌ No Android devices found."
    echo ""
    echo "   Troubleshooting:"
    echo "   1. Connect your phone via USB"
    echo "   2. Enable USB Debugging on your phone:"
    echo "      Settings → Developer Options → USB Debugging ON"
    echo "   3. Check phone for 'Allow USB Debugging?' prompt"
    echo "   4. Run this script again"
    echo ""
    echo "   Still not working? Try:"
    echo "   • Different USB cable (some are charge-only)"
    echo "   • Different USB port on computer"
    echo "   • Restart ADB server: adb kill-server && adb start-server"
    exit 1
fi

echo "✅ Found $DEVICES connected device(s)"
adb devices
echo ""

# Ask which build type
echo "Which version do you want to install?"
echo "1) Debug (faster build, larger file, ~15-20 MB)"
echo "2) Release (optimized, smaller file, ~8-12 MB, requires signing)"
read -p "Enter choice [1-2]: " choice

case $choice in
    1)
        BUILD_TYPE="debug"
        echo ""
        echo "📦 Building debug APK..."

        # Try gradle first, then gradlew
        if command -v gradle &> /dev/null; then
            gradle assembleDebug
        elif [ -f "./gradlew" ]; then
            ./gradlew assembleDebug
        else
            echo "❌ Neither gradle nor gradlew found."
            echo "   Please install Gradle or run from project root with gradlew."
            exit 1
        fi

        APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
        ;;
    2)
        BUILD_TYPE="release"
        echo ""
        echo "⚠️  Building release APK requires signing configuration."
        echo "   See mikestarthere.md for setup instructions."
        echo ""
        read -p "Continue anyway? [y/N]: " confirm

        if [[ ! $confirm =~ ^[Yy]$ ]]; then
            echo "Cancelled. Use debug build instead (option 1)."
            exit 0
        fi

        echo ""
        echo "📦 Building release APK..."

        if command -v gradle &> /dev/null; then
            gradle assembleRelease
        elif [ -f "./gradlew" ]; then
            ./gradlew assembleRelease
        else
            echo "❌ Neither gradle nor gradlew found."
            exit 1
        fi

        APK_PATH="app/build/outputs/apk/release/app-release.apk"
        ;;
    *)
        echo "❌ Invalid choice"
        exit 1
        ;;
esac

# Check if build succeeded
BUILD_EXIT_CODE=$?
if [ $BUILD_EXIT_CODE -ne 0 ]; then
    echo ""
    echo "❌ Build failed with exit code $BUILD_EXIT_CODE"
    echo "   Check the error messages above."
    echo ""
    echo "   Common fixes:"
    echo "   • Run: gradle clean"
    echo "   • Check Java version: java -version (need JDK 8+)"
    echo "   • Check Gradle version: gradle --version"
    exit 1
fi

# Check if APK was created
if [ ! -f "$APK_PATH" ]; then
    echo ""
    echo "❌ APK not found at: $APK_PATH"
    echo "   Build may have failed. Check the output above."
    exit 1
fi

# Get APK size
APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
echo ""
echo "✅ Build successful! APK size: $APK_SIZE"
echo "   Location: $APK_PATH"

# Ask for confirmation before installing
echo ""
read -p "📲 Install on connected device now? [Y/n]: " install_confirm

if [[ $install_confirm =~ ^[Nn]$ ]]; then
    echo ""
    echo "✅ Build complete. APK saved at:"
    echo "   $APK_PATH"
    echo ""
    echo "To install manually:"
    echo "   adb install -r $APK_PATH"
    exit 0
fi

echo ""
echo "📲 Installing APK on device..."
echo "   (If phone screen is locked, unlock it now)"

# Install with replace flag (-r) to upgrade existing installation
adb install -r "$APK_PATH"
INSTALL_EXIT_CODE=$?

if [ $INSTALL_EXIT_CODE -eq 0 ]; then
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🎉 SUCCESS! Frontier Command Center installed!"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "📱 The app is now on your phone!"
    echo "   Look for 'Frontier Command Center' in your app drawer"
    echo ""
    echo "🏕️  The app includes 10 educational camps:"
    echo ""
    echo "   📡 Camp 1: REST API Basics"
    echo "   🔌 Camp 2: WebSocket Fundamentals"
    echo "   📍 Camp 3: GPS Integration"
    echo "   💻 Camp 4: Command Console"
    echo "   🔄 Camp 5: State Management"
    echo "   🧭 Camp 6: Advanced Navigation"
    echo "   💾 Camp 7: Data Persistence"
    echo "   ⚙️  Camp 8: Background Processing"
    echo "   🔔 Camp 9: System Integration"
    echo "   🚀 Camp 10: Deployment & Release"
    echo ""
    echo "📚 Start with Camp 1 and work through all 10!"
    echo "❓ Tap the Help button (?) in the app for guides"
    echo ""
    echo "Permissions you'll be asked for:"
    echo "   • Location (Camp 3) - for GPS demo"
    echo "   • Notifications (Camp 9) - for notification demo"
    echo ""
    echo "Happy coding! 🎓"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
else
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "❌ Installation failed with exit code $INSTALL_EXIT_CODE"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "Common issues and solutions:"
    echo ""
    echo "1. Phone is locked"
    echo "   → Unlock your phone and try again"
    echo ""
    echo "2. USB debugging not authorized"
    echo "   → Check your phone for 'Allow USB Debugging?' prompt"
    echo "   → Select 'Always allow from this computer'"
    echo ""
    echo "3. App already installed (signature mismatch)"
    echo "   → Uninstall the old version first:"
    echo "     adb uninstall com.frontiercommand"
    echo "   → Then run this script again"
    echo ""
    echo "4. Insufficient storage"
    echo "   → Free up ~20 MB on your phone"
    echo ""
    echo "5. Installation blocked"
    echo "   → Go to Settings → Apps → Special Access"
    echo "   → Install Unknown Apps → Enable for your file manager"
    echo ""
    echo "Still having issues?"
    echo "   → Check detailed troubleshooting in mikestarthere.md"
    echo "   → View logs: adb logcat | grep -i frontiercommand"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    exit 1
fi
