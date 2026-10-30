# Flow Edge - Native Android App in Kotlin (Jetpack Compose)

**Flow Edge** is a production-grade, highly customizable Edge Panel native Android application built with **Kotlin** and modern **Jetpack Compose**.
Package: `com.ecrisar.flow.edge`

---

## 🌟 Key Features

### 1. Flow Edge Handle & Position Customization
- **Right-Edge Placement**: Sits seamlessly along the right screen edge with authentic One UI translucent pill aesthetics.
- **Drag-to-Adjust Position**: Touch, hold, and drag vertically up/down to reposition the edge handle anywhere along the screen edge with real-time percentage feedback and haptics.
- **Customizable Appearance**:
  - Vertical offset (0% to 100% of screen height)
  - Handle thickness & length
  - Signature accent colors (Electric Blue, Violet, Pink, Emerald, Amber, Frost White)
  - Transparency & alpha controls
  - Vibration haptic feedback toggle

### 2. Multi-Panel Horizontal Carousel (Swipe to Switch)
Swipe horizontally across panels with fluid paging animations and One UI indicator dots:
- 📱 **Apps Edge**:
  - Favorite quick-access apps with custom icon badges.
  - "All Apps" toggle button with instant search filter.
  - Quick launch on tap.
  - Add/remove apps to favorites with the star toggle.
- 📝 **Quick Notes**:
  - Instant memo capture directly from the edge drawer.
  - Color-coded note cards with title and body.
  - Persistent storage across sessions.
- 📅 **Calendar & Agenda**:
  - Compact monthly calendar grid with month navigation (`<` / `>`).
  - Day selector highlighting Today and active events.
  - Daily agenda schedule view with quick event creator (`+`).
- 🧮 **Calculator**:
  - Full-featured floating keypad (`0-9`, `+`, `-`, `×`, `÷`, `%`, `+/-`, `C`, `⌫`, `=`).
  - Instant arithmetic expression evaluation engine.
- 🛠️ **Quick Tools**:
  - Flashlight switch with status indicator.
  - Digital ruler with CM / INCH scale switches.
  - Quick volume slider.

### 3. Drag-and-Drop Pop-up View Multitasking
- **Drag App to Screen**: Long-press any app in the Apps Edge panel and drag it out into the workspace.
- **Visual Drop Zone**: Glowing guidance indicator (*"Drop here to open in Pop-up view"*).
- **Hybrid Pop-up Architecture**:
  1. **In-App Movable Pop-up View**: Spawns a floating, draggable, resizable window container complete with window controls (drag title bar to move, corner handle to resize, minimize to floating circle bubble, maximize, and close).
  2. **Android OS Native Freeform Mode**: Triggers `ActivityOptions.setLaunchBounds(...)` and `Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK` (with freeform reflection) so supported devices launch the real system app directly into Android OS Pop-up View!

### 4. Dual-Mode Operation (In-App Container + System-Wide Overlay)
- **In-App Container & Simulator**: Directly test, customize, and multitask inside the app with sample apps and floating windows.
- **System-Wide Floating Overlay (`SYSTEM_ALERT_WINDOW`)**:
  - Runs in the background via `EdgeOverlayService` (`TYPE_APPLICATION_OVERLAY`).
  - Keeps the edge handle accessible over any app on your device (browser, social media, games).
  - Built-in overlay permission request handler (`Settings.ACTION_MANAGE_OVERLAY_PERMISSION`).

---

## 🏗️ Architecture & Project Structure

```
FlowEdge/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── res/
│   │   │   ├── values/ (colors.xml, strings.xml, themes.xml)
│   │   │   └── drawable/ (ic_launcher_foreground.xml, etc.)
│   │   └── java/com/ecrisar/flow/edge/
│   │       ├── MainActivity.kt               # Main entry point & desktop simulator
│   │       ├── model/
│   │       │   ├── PanelType.kt              # Enum for edge panels
│   │       │   ├── AppItem.kt                # Model for launcher apps
│   │       │   ├── EdgePanelSettings.kt      # Handle & panel configuration model
│   │       │   ├── NoteItem.kt               # Model for quick notes
│   │       │   ├── CalendarData.kt           # Calendar event models
│   │       │   └── PopupWindowState.kt       # Pop-up window position & size state
│   │       ├── data/
│   │       │   ├── SettingsRepository.kt     # SharedPreferences persistence
│   │       │   ├── AppRepository.kt          # PackageManager loader & favorites
│   │       │   ├── NotesRepository.kt        # Notes CRUD & JSON persistence
│   │       │   ├── CalendarRepository.kt     # Events and date schedule store
│   │       │   └── LauncherHelper.kt         # Freeform/pop-up window launch helper
│   │       ├── ui/
│   │       │   ├── theme/                    # Colors, Typography, Theme
│   │       │   ├── components/
│   │       │   │   ├── GlassmorphicCard.kt   # Frosted acrylic surface
│   │       │   │   ├── EdgeHandleView.kt     # Movable right-edge handle pill
│   │       │   │   ├── DragDropState.kt      # Pointer drag state & drop indicator
│   │       │   │   └── FloatingPopupView.kt  # Draggable & resizable pop-up window
│   │       │   ├── panels/
│   │       │   │   ├── AppsPanel.kt          # App grid & drag-to-pop-up
│   │       │   │   ├── NotesPanel.kt         # Quick notepad panel
│   │       │   │   ├── CalendarPanel.kt      # Interactive calendar panel
│   │       │   │   ├── CalculatorPanel.kt    # Calculator panel
│   │       │   │   └── ToolsPanel.kt         # Flashlight, ruler & volume panel
│   │       │   ├── edge/
│   │       │   │   ├── EdgePanelDrawer.kt    # Slide-out drawer with HorizontalPager
│   │       │   │   └── EdgeContainerView.kt  # Root coordinator for handle & panels
│   │       │   └── settings/
│   │       │       └── EdgeSettingsScreen.kt # Full customization settings screen
│   │       └── service/
│   │           └── EdgeOverlayService.kt     # Background WindowManager overlay
│   └── build.gradle.kts
├── settings.gradle.kts
├── build.gradle.kts
└── gradle.properties
```

---

## 🚀 How to Build and Run

### Option 1: Android Studio (Recommended)
1. Open **Android Studio** (Hedgehog, Iguana, Koala, or later).
2. Select **Open** and choose the `FlowEdge` folder.
3. Allow Gradle to sync dependencies automatically.
4. Connect an Android device (Android 8.0+ / API 26+) or launch an Android emulator.
5. Click **Run ▶** to install and launch.

### Option 2: Command Line (Gradle)
```bash
./gradlew assembleDebug
```
The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`
