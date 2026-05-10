package com.example.weathersnap.domain.model

/**
 * Maps Open-Meteo WMO weather interpretation codes to human-readable condition strings.
 * Reference: https://open-meteo.com/en/docs#weathervariables
 */
fun weatherCodeToCondition(code: Int): String = when (code) {
    0 -> "Clear Sky"
    1 -> "Mainly Clear"
    2 -> "Partly Cloudy"
    3 -> "Overcast"
    45 -> "Foggy"
    48 -> "Icy Fog"
    51 -> "Light Drizzle"
    53 -> "Moderate Drizzle"
    55 -> "Dense Drizzle"
    61 -> "Slight Rain"
    63 -> "Moderate Rain"
    65 -> "Heavy Rain"
    71 -> "Slight Snowfall"
    73 -> "Moderate Snowfall"
    75 -> "Heavy Snowfall"
    77 -> "Snow Grains"
    80 -> "Slight Showers"
    81 -> "Moderate Showers"
    82 -> "Violent Showers"
    85 -> "Slight Snow Showers"
    86 -> "Heavy Snow Showers"
    95 -> "Thunderstorm"
    96 -> "Thunderstorm with Hail"
    99 -> "Thunderstorm with Heavy Hail"
    else -> "Unknown"
}
