package com.example.weathersnap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary            = PrimaryGreen,
    onPrimary          = OnPrimaryGreen,
    primaryContainer   = PrimaryContainer,
    onPrimaryContainer = PrimaryGreen,
    secondary          = SecondaryGreen,
    onSecondary        = OnSecondaryGreen,
    background         = BackgroundDark,
    onBackground       = TextPrimary,
    surface            = SurfaceDark,
    onSurface          = TextPrimary,
    surfaceVariant     = SurfaceVariantDark,
    onSurfaceVariant   = TextSecondary,
    error              = ErrorRed,
    onError            = OnErrorRed,
    outline            = OutlineGreen
)

@Composable
fun WeatherSnapTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography,
        content     = content
    )
}