package com.example.weathersnap.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weathersnap.presentation.ui.CreateReportScreen
import com.example.weathersnap.presentation.ui.CustomCameraScreen
import com.example.weathersnap.presentation.ui.SavedReportsScreen
import com.example.weathersnap.presentation.ui.WeatherScreen
import com.example.weathersnap.presentation.viewmodel.ReportViewModel
import com.example.weathersnap.presentation.viewmodel.WeatherUiState
import com.example.weathersnap.presentation.viewmodel.WeatherViewModel

object Routes {
    const val WEATHER       = "weather"
    const val CREATE_REPORT = "create_report"
    const val CAMERA        = "camera"
    const val SAVED_REPORTS = "saved_reports"
    const val GRAPH_ROOT    = "root"
}

@Composable
fun WeatherSnapNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController   = navController,
        startDestination = Routes.WEATHER,
        route            = Routes.GRAPH_ROOT
    ) {

        // ── Weather Search Screen ─────────────────────────────────────────────
        composable(Routes.WEATHER) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.GRAPH_ROOT)
            }
            val weatherViewModel: WeatherViewModel = hiltViewModel(parentEntry)

            WeatherScreen(
                viewModel     = weatherViewModel,
                onCreateReport = { navController.navigate(Routes.CREATE_REPORT) },
                onSavedReports = { navController.navigate(Routes.SAVED_REPORTS) }
            )
        }

        // ── Create Report Screen ──────────────────────────────────────────────
        composable(Routes.CREATE_REPORT) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.GRAPH_ROOT)
            }
            val weatherViewModel: WeatherViewModel = hiltViewModel(parentEntry)
            val reportViewModel: ReportViewModel   = hiltViewModel()

            // Camera result arrives here via savedStateHandle after popBackStack()
            val capturedImagePath by backStackEntry.savedStateHandle
                .getStateFlow("capturedImagePath", "")
                .collectAsState()

            val weatherData = (weatherViewModel.uiState.collectAsState().value
                    as? WeatherUiState.Success)?.data

            if (weatherData != null) {
                CreateReportScreen(
                    weatherData        = weatherData,
                    capturedImagePath  = capturedImagePath,
                    onCapturePhoto     = { navController.navigate(Routes.CAMERA) },
                    onBack             = { navController.popBackStack() },
                    reportViewModel    = reportViewModel
                )
            }
        }

        // ── CameraX Screen ────────────────────────────────────────────────────
        composable(Routes.CAMERA) {
            CustomCameraScreen(
                onImageCaptured = { path ->
                    // Pass image path back to CreateReportScreen via savedStateHandle
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("capturedImagePath", path)
                    navController.popBackStack()
                },
                onClose = { navController.popBackStack() }
            )
        }

        // ── Saved Reports Screen ──────────────────────────────────────────────
        composable(Routes.SAVED_REPORTS) {
            val reportViewModel: ReportViewModel = hiltViewModel()
            SavedReportsScreen(
                viewModel = reportViewModel,
                onBack    = { navController.popBackStack() }
            )
        }
    }
}
