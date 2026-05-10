package com.example.weathersnap.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersnap.presentation.viewmodel.WeatherUiState
import com.example.weathersnap.presentation.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onCreateReport: () -> Unit,
    onSavedReports: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    var query by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "WeatherSnap",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = onSavedReports) {
                        Icon(Icons.Default.List, contentDescription = "Saved Reports")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Search Row
            ExposedDropdownMenuBox(
                expanded = showSuggestions && suggestions.isNotEmpty(),
                onExpandedChange = {}
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { q ->
                        query = q
                        showSuggestions = true
                        viewModel.fetchSuggestions(q)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    placeholder = { Text("Search city…", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    trailingIcon = {
                        IconButton(onClick = {
                            showSuggestions = false
                            viewModel.clearSuggestions()
                            focusManager.clearFocus()
                            viewModel.fetchWeather(query)
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        showSuggestions = false
                        viewModel.clearSuggestions()
                        focusManager.clearFocus()
                        viewModel.fetchWeather(query)
                    }),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = showSuggestions && suggestions.isNotEmpty(),
                    onDismissRequest = { showSuggestions = false },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    suggestions.forEach { suggestion ->
                        DropdownMenuItem(
                            text = { Text(suggestion, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                query = suggestion
                                showSuggestions = false
                                viewModel.clearSuggestions()
                                focusManager.clearFocus()
                                viewModel.fetchWeather(suggestion)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            //  State Display
            when (val state = uiState) {
                WeatherUiState.Empty -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌤️", fontSize = 64.sp)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Search for a city to get started",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                WeatherUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is WeatherUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(
                                "⚠️ ${state.message}",
                                modifier = Modifier.padding(20.dp),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                is WeatherUiState.Success -> {
                    val data = state.data
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically { it / 2 } + fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            // Weather Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text(
                                        data.cityName,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        data.condition,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        "${data.temperature}°C",
                                        fontSize = 52.sp,
                                        fontWeight = FontWeight.Light,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                    Spacer(Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        WeatherStat("💧", "Humidity", "${data.humidity}%")
                                        WeatherStat("💨", "Wind", "${data.windSpeed} km/h")
                                        WeatherStat("🔵", "Pressure", "${data.pressure} hPa")
                                    }
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            Button(
                                onClick = onCreateReport,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("📷  Create Report", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherStat(emoji: String, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}
