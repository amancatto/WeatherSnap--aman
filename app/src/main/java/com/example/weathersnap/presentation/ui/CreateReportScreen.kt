package com.example.weathersnap.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weathersnap.data.local.WeatherReportEntity
import com.example.weathersnap.domain.model.WeatherData
import com.example.weathersnap.presentation.viewmodel.ReportSaveState
import com.example.weathersnap.presentation.viewmodel.ReportViewModel
import com.example.weathersnap.utils.ImageCompressor
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    weatherData: WeatherData,
    capturedImagePath: String,
    onCapturePhoto: () -> Unit,
    onBack: () -> Unit,
    reportViewModel: ReportViewModel
) {
    val context = LocalContext.current
    val saveState by reportViewModel.saveState.collectAsState()
    var notes by remember { mutableStateOf("") }

    // Navigate back on success
    LaunchedEffect(saveState) {
        if (saveState is ReportSaveState.Success) {
            reportViewModel.resetSaveState()
            onBack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Create Report", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Weather Summary Card ─────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                weatherData.cityName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                weatherData.condition,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            "${weatherData.temperature}°C",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text("💧 ${weatherData.humidity}%", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("💨 ${weatherData.windSpeed} km/h", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("🔵 ${weatherData.pressure} hPa", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Photo Preview ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (capturedImagePath.isNotBlank()) {
                    AsyncImage(
                        model = capturedImagePath,
                        contentDescription = "Captured photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📷", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No photo yet",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCapturePhoto,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("📷  Capture Photo", fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(20.dp))

            // ── Field Notes ──────────────────────────────────────────────────
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Field notes (optional)…", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                label = { Text("Notes") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.height(24.dp))

            // ── Save Button ──────────────────────────────────────────────────
            val isSaving = saveState is ReportSaveState.Saving
            Button(
                onClick = {
                    if (capturedImagePath.isNotBlank()) {
                        val compression = runCatching {
                            ImageCompressor.compress(context, java.io.File(capturedImagePath))
                        }.getOrNull()

                        val entity = WeatherReportEntity(
                            id                  = UUID.randomUUID().toString(),
                            cityName            = weatherData.cityName,
                            temperature         = weatherData.temperature,
                            condition           = weatherData.condition,
                            humidity            = weatherData.humidity,
                            windSpeed           = weatherData.windSpeed,
                            pressure            = weatherData.pressure,
                            imagePath           = compression?.compressedFile?.absolutePath ?: capturedImagePath,
                            originalImageSize   = compression?.originalSizeBytes ?: 0L,
                            compressedImageSize = compression?.compressedSizeBytes ?: 0L,
                            notes               = notes.trim(),
                            timestamp           = System.currentTimeMillis()
                        )
                        reportViewModel.saveReport(entity)
                    }
                },
                enabled = capturedImagePath.isNotBlank() && !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.outline
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("💾  Save Report", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            if (saveState is ReportSaveState.Error) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Error: ${(saveState as ReportSaveState.Error).message}",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
