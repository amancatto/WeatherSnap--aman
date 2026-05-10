package com.example.weathersnap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersnap.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    // ─── Weather fetch state ──────────────────────────────────────────────────
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    // ─── Autocomplete suggestions ─────────────────────────────────────────────
    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    // ─── In-memory LRU weather cache ─────────────────────────────────────────
    private val weatherCache = object : LinkedHashMap<String, WeatherUiState.Success>(
        MAX_CACHE, 0.75f, true
    ) {
        override fun removeEldestEntry(eldest: Map.Entry<String, WeatherUiState.Success>) =
            size > MAX_CACHE
    }

    private var suggestionJob: Job? = null

    // ─── Autocomplete: debounced, min 2 chars ─────────────────────────────────
    fun fetchSuggestions(query: String) {
        suggestionJob?.cancel()
        if (query.length < 2) { _suggestions.value = emptyList(); return }
        suggestionJob = viewModelScope.launch {
            delay(300) // debounce
            repository.searchCitySuggestions(query)
                .onSuccess { _suggestions.value = it }
                .onFailure { _suggestions.value = emptyList() }
        }
    }

    fun clearSuggestions() { _suggestions.value = emptyList() }

    // ─── Full weather fetch (with cache) ─────────────────────────────────────
    fun fetchWeather(cityName: String) {
        val key = cityName.trim().lowercase()
        if (key.isBlank()) { _uiState.value = WeatherUiState.Empty; return }

        weatherCache[key]?.let { _uiState.value = it; return }

        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            // Extract just the city part (before first comma) for the API call
            val apiQuery = cityName.substringBefore(",").trim()
            repository.getWeatherByCity(apiQuery)
                .onSuccess { data ->
                    val success = WeatherUiState.Success(data)
                    weatherCache[key] = success
                    _uiState.value = success
                }
                .onFailure { _uiState.value = WeatherUiState.Error(it.message ?: "Error") }
        }
    }

    fun resetState() { _uiState.value = WeatherUiState.Empty }

    companion object { private const val MAX_CACHE = 20 }
}
