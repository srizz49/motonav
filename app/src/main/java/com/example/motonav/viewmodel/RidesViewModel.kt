package com.example.motonav.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motonav.data.model.RideHistory
import com.example.motonav.data.model.RouteType
import com.example.motonav.data.repository.RidesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RidesUiState(
    val rides: List<RideHistory> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RidesViewModel(
    private val ridesRepository: RidesRepository
) : ViewModel() {

    val rideHistory: StateFlow<List<RideHistory>> = ridesRepository.rideHistory

    private val _uiState = MutableStateFlow(RidesUiState())
    val uiState: StateFlow<RidesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            rideHistory.collect {
                _uiState.value = _uiState.value.copy(rides = it)
            }
        }
    }

    fun refreshRides() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                ridesRepository.refreshRides()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Failed to load rides.")
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun recordRide(
        title: String,
        distanceKm: Double,
        durationMinutes: Int,
        fuelCostInr: Double,
        routeType: RouteType,
        bikeModel: String
    ) {
        viewModelScope.launch {
            ridesRepository.saveCompletedRide(
                title = title,
                distanceKm = distanceKm,
                durationMinutes = durationMinutes,
                fuelCostInr = fuelCostInr,
                routeType = routeType,
                bikeModel = bikeModel
            )
        }
    }
}
