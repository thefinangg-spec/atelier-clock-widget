# Atelier Clock — Android Glance Clock Widget

This repository contains the complete, production-grade Android Studio source code for **Atelier Clock**, featuring:

- **Kotlin + Jetpack Compose + AndroidX Glance** (\`androidx.glance:glance-appwidget:1.1.1\`)
- **6 Distinct Visual Styles**:
  1. Minimal
  2. Editorial
  3. Digital
  4. Terminal
  5. Typographic
  6. Glass
- **Adaptive Home Screen Layouts**: Responsive sizes across Small (2x2), Medium (4x2), and Large (4x4)
- **Lifecycle & Battery Preservation**: Periodic WorkManager synchronization, broadcast receivers for \`BOOT_COMPLETED\` and \`TIMEZONE_CHANGED\`.
- **In-App & Launcher Customization**: Configuration activity and DataStore preference synchronization.

## Build Instructions
1. Open this directory in **Android Studio** (2024.2+).
2. Sync Gradle files (JDK 17).
3. Build & Run: \`./gradlew assembleDebug\` or click **Run**.
4. On your emulator or physical Android device, long-press the home screen, choose **Widgets**, select **Atelier Clock**, and place it on your home screen.
