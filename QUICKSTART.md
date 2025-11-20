# Quick Start - 3 Simple Steps

Get the app running on your phone in under 5 minutes!

---

## Method 1: Automated Script (Easiest)

```bash
cd /home/user/Android
./install-app.sh
```

**That's it!** The script will build and install the app automatically.

---

## Method 2: Manual (3 Commands)

```bash
# 1. Enable USB debugging on your phone (one-time setup)
#    Settings → Developer Options → USB Debugging ON

# 2. Connect phone via USB

# 3. Run these commands:
cd /home/user/Android
gradle assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Done!** Open "Frontier Command Center" on your phone.

---

## First Time Setup (Phone)

If you've never used USB debugging before:

1. **Enable Developer Mode:**
   - Go to: Settings → About Phone
   - Tap "Build Number" 7 times
   - You'll see: "You are now a developer!"

2. **Enable USB Debugging:**
   - Go to: Settings → Developer Options
   - Turn ON "USB Debugging"

3. **Connect & Authorize:**
   - Connect phone to computer via USB
   - When prompted on phone: "Allow USB Debugging?"
   - Check "Always allow from this computer"
   - Tap "OK"

**Now run the installation script or manual commands above!**

---

## Troubleshooting

### "ADB not found"
```bash
# Install ADB:
# Ubuntu/Debian:
sudo apt install adb

# macOS:
brew install android-platform-tools
```

### "No devices found"
- Is USB debugging enabled?
- Is phone unlocked?
- Did you allow USB debugging on phone?
- Try different USB cable/port

### "Installation failed"
```bash
# Uninstall old version:
adb uninstall com.frontiercommand

# Then install again:
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## What's in the App?

**10 Educational Camps:**
1. REST API Basics
2. WebSocket Fundamentals
3. GPS Integration
4. Command Console
5. State Management
6. Advanced Navigation
7. Data Persistence
8. Background Processing
9. System Integration
10. Deployment & Release

**Plus:**
- Settings (theme, logs, cache)
- Help documentation
- Interactive demos
- 210+ tests (all passing)

---

## Need More Help?

See **`mikestarthere.md`** for:
- Detailed installation options
- Building release APK
- Publishing to Play Store
- Complete troubleshooting guide
- System requirements
- Testing checklist

---

**That's all you need! Enjoy your frontier expedition! 🏕️**
