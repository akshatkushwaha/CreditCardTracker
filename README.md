# CreditCardTracker

CreditCardTracker is an Android app built with Kotlin and Jetpack Compose to help manage credit cards, billing cycles, and payment history.

## Overview

This project includes a single Android module named `app` with:
- Jetpack Compose UI
- Room database for storing credit card and bill history data
- Biometric authentication for viewing sensitive card details (PIN/CVV)
- WorkManager-based daily notifications for billing and unpaid reminders
- Navigation between list, add/edit, and detail screens

## Key Features

- Add and edit credit cards
- View credit card details and billing history
- Biometric or device credential unlock for sensitive card information
- Daily notification scheduling for billing reminders
- Sample card and billing history data auto-populated on first launch

## Tech Stack

- Kotlin
- Android Jetpack Compose
- Room
- WorkManager
- Navigation Compose
- Biometric APIs
- Kotlin Serialization
- Gradle Version Catalog (`libs.versions.toml`)

## Project Structure

- `app/` - Android application module
- `app/src/main/java/com/example/creditcardtracker/` - source code
- `app/src/main/AndroidManifest.xml` - manifest with biometric and notification permissions
- `app/build.gradle.kts` - app module Gradle configuration
- `build.gradle.kts` - root Gradle configuration
- `settings.gradle.kts` - included modules

## Requirements

- Android Studio with Kotlin and Compose support
- Gradle wrapper (`./gradlew`)
- Java 17 compatibility (configured in the project)
- Minimum SDK: 36
- Compile SDK: 36

## Build and Run

From the project root:

```bash
./gradlew assembleDebug
```

Or open the project in Android Studio and run the `app` module.

## Tests

Run unit tests with:

```bash
./gradlew test
```

Run instrumentation tests with:

```bash
./gradlew connectedAndroidTest
```

## Notes

- The app uses biometric authentication if available, with a device credential fallback.
- `NotificationWorker` schedules daily work to check billing dates and send reminders.
- Sample credit cards are inserted automatically when the app is first launched.

## Application Details

- Application ID: `com.example.creditcardtracker`
- Package namespace: `com.example.creditcardtracker`
- App label comes from `strings.xml`

## License

This repository does not include a specific license file. Add one if you plan to share or publish this project.
