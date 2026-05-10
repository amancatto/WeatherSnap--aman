package com.example.weathersnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.weathersnap.presentation.navigation.WeatherSnapNavGraph
import com.example.weathersnap.ui.theme.WeatherSnapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherSnapTheme {
                WeatherSnapNavGraph()
            }
        }
    }
}