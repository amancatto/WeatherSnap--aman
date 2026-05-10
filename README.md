<div align="center">

# WeatherSnap 📸🌤️

<img src="https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExajdqaTlucGRnMmcxOTM1eTVkczVxdmozZm5yeWU1d280NncxYXUxaCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/Sm9AfJRiZofjlrkAAl/giphy.gif" width="320" alt="WeatherSnap demo"/>

### An Android app that captures photos and automatically attaches real-time weather data to each image.
### Search a city → fetch live weather via Open-Meteo (no API key) → snap a photo with CameraX → save a compressed weather report locally with Room.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-4285F4?style=flat&logo=android)](https://developer.android.com/jetpack/compose)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

</div>

---


## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (Model-View-ViewModel) |
| DI | Hilt (Dagger) |
| Async | Kotlin Coroutines + StateFlow |
| Networking | Retrofit 2 + OkHttp (Logging Interceptor) |
| Weather API | Open-Meteo (free, no API key required) |
| Database | Room (SQLite) |
| Camera | CameraX (no device intents) |
| Image Loading | Coil |
| Navigation | Navigation Compose |

---

## Weather API (Open-Meteo)

No API key required.

| Endpoint | URL |
|---|---|
| Geocoding | `https://geocoding-api.open-meteo.com/v1/search` |
| Forecast | `https://api.open-meteo.com/v1/forecast` |

---

## MVVM Package Structure

```
com.example.weathersnap/
│
├── di/                          # Dependency Injection (Hilt Modules)
│   └── AppModule.kt
│
├── data/
│   ├── local/                   # Room Database
│   │   ├── WeatherReportEntity.kt
│   │   ├── ReportDao.kt
│   │   └── WeatherDatabase.kt
│   │
│   ├── remote/                  # Retrofit / Network
│   │   ├── WeatherApiService.kt
│   │   └── dto/
│   │       ├── GeocodingResponse.kt
│   │       └── ForecastResponse.kt
│   │
│   └── repository/              # Repository Implementations
│       └── WeatherRepositoryImpl.kt
│
├── domain/
│   ├── model/                   # Domain/Business Models
│   │   ├── WeatherData.kt
│   │   └── WeatherUtils.kt
│   │
│   └── repository/              # Repository Interfaces (contracts)
│       └── WeatherRepository.kt
│
└── presentation/
    ├── camera/                  # CameraX Screen
    │   ├── CameraScreen.kt
    │   └── CameraViewModel.kt
    │
    ├── weather/                 # Weather Detail Screen
    │   ├── WeatherScreen.kt
    │   └── WeatherViewModel.kt
    │
    └── history/                 # Saved Reports Screen
        ├── HistoryScreen.kt
        └── HistoryViewModel.kt
```

---

## Strict Rules Applied

- ✅ No OpenWeatherMap — uses Open-Meteo (free, no key)
- ✅ CameraX only — no `Intent(MediaStore.ACTION_IMAGE_CAPTURE)`
- ✅ All database/network primary IDs are `String` (UUID)
- ✅ IO Dispatcher used for all Room operations
- ✅ Coroutines + Flow for reactive data

---

## How to Run

### Prerequisites
- Android Studio Ladybug (2024.x) or newer
- Android SDK 36
- An Android emulator or physical device (API 24+)

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/WeatherSnap.git
   cd WeatherSnap
   ```

2. Open in Android Studio.

3. Let Gradle sync complete automatically.

4. Run the app on an emulator or device:
   ```
   Run > Run 'app'
   ```

> **Note:** No API keys are needed. Open-Meteo is a free, open-source weather API.

---

## Database Schema

**Table: `weather_reports`**

| Column | Type | Description |
|---|---|---|
| `id` | `TEXT` (UUID) | Primary Key |
| `cityName` | `TEXT` | City name from geocoding |
| `temperature` | `REAL` | Temperature in °C |
| `condition` | `TEXT` | Human-readable weather condition |
| `humidity` | `INTEGER` | Relative humidity (%) |
| `windSpeed` | `REAL` | Wind speed (km/h) |
| `pressure` | `REAL` | Surface pressure (hPa) |
| `imagePath` | `TEXT` | Absolute path to captured image |
| `originalImageSize` | `INTEGER` | Original image size in bytes |
| `compressedImageSize` | `INTEGER` | Compressed image size in bytes |
| `notes` | `TEXT` | Optional user notes |
| `timestamp` | `INTEGER` | Unix timestamp (ms) |
