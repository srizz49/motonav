package com.example.motonav.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NavigationUiState(
    val nextInstruction: String = "In 300m, Turn Right onto Amby Valley Hairpin Road",
    val secondaryInstruction: String = "Then continue on Scenic Route for 14.2 km",
    val distanceToNextTurn: String = "300 m",
    val currentSpeedKmh: Int = 54,
    val speedLimitKmh: Int = 60,
    val remainingDistanceKm: Double = 38.4,
    val remainingMinutes: Int = 45,
    val etaString: String = "11:42 AM",
    val isMuted: Boolean = false,
    val isNavigating: Boolean = true,
    val hasLocationPermission: Boolean = true
)

class NavigationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NavigationUiState())
    val uiState: StateFlow<NavigationUiState> = _uiState.asStateFlow()

    init {
        simulateNavigationProgress()
    }

    fun toggleMute() {
        _uiState.value = _uiState.value.copy(isMuted = !_uiState.value.isMuted)
    }

    fun setLocationPermissionGranted(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasLocationPermission = granted)
    }

    fun stopNavigation() {
        _uiState.value = _uiState.value.copy(isNavigating = false)
    }

    private fun simulateNavigationProgress() {
        viewModelScope.launch {
            while (true) {
                delay(4000)
                val current = _uiState.value
                if (!current.isNavigating) break
                val newRemKm = (current.remainingDistanceKm - 0.2).coerceAtLeast(0.0)
                val newSpeed = (48..65).random()
                _uiState.value = current.copy(
                    remainingDistanceKm = (newRemKm * 10).toInt() / 10.0,
                    currentSpeedKmh = newSpeed
                )
            }
        }
    }
}
