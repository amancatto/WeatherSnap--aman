package com.example.weathersnap.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weathersnap.data.local.WeatherReportEntity
import com.example.weathersnap.presentation.viewmodel.ReportViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedReportsScreen(
    viewModel: ReportViewModel,
    onBack: () -> Unit
) {
    val reports by viewModel.allReports.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Saved Reports", fontWeight = FontWeight.Bold) },
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
        if (reports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📂", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No reports yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Capture your first weather snapshot!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reports, key = { it.id }) { report ->
                    ReportCard(report = report, onDelete = { viewModel.deleteReport(report.id) })
                }
            }
        }
    }
}

@Composable
private fun ReportCard(
    report: WeatherReportEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            //  Image
            if (report.imagePath.isNotBlank()) {
                AsyncImage(
                    model = report.imagePath,
                    contentDescription = "Report photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                //  Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            report.cityName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            report.condition,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        "${report.temperature}°C",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.height(10.dp))

                //  Weather stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("💧 ${report.humidity}%", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("💨 ${report.windSpeed} km/h", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("🔵 ${report.pressure} hPa", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline)

                //  Image size comparison
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Original", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatBytes(report.originalImageSize), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                    }
                    Text("→", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterVertically))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Compressed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatBytes(report.compressedImageSize), fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    }
                }

                //  Notes
                if (report.notes.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "\"${report.notes}\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                Spacer(Modifier.height(10.dp))

                //  Timestamp + Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        formatTimestamp(report.timestamp),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_000_000 -> "%.2f MB".format(bytes / 1_000_000.0)
    bytes >= 1_000     -> "%.1f KB".format(bytes / 1_000.0)
    else               -> "$bytes B"
}

private fun formatTimestamp(millis: Long): String =
    SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault()).format(Date(millis))
