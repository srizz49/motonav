package com.example.motonav.viewmodel

import androidx.lifecycle.ViewModel
import com.example.motonav.data.model.RouteInfo
import com.example.motonav.data.model.RouteType
import com.example.motonav.data.model.Waypoint
import com.example.motonav.data.repository.RouteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RouteUiState(
    val selectedStartLocation: String = "Pune City Center",
    val selectedDestination: String = "Mahabaleshwar Peak",
    val scenicRoute: RouteInfo,
    val fastestRoute: RouteInfo,
    val selectedRouteType: RouteType = RouteType.SCENIC,
    val waypoints: List<Waypoint> = emptyList(),
    val searchResults: List<String> = listOf(
        "Mahabaleshwar Peak",
        "Lonavala Tiger Point",
        "Tamhini Ghat Pass",
        "Malshej Ghat Waterfall",
        "Lavasa Hill City",
        "Alibaug Coastal Road"
    )
)

class RouteViewModel(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        RouteUiState(
            scenicRoute = routeRepository.scenicRoute.value,
            fastestRoute = routeRepository.fastestRoute.value,
            waypoints = routeRepository.waypoints.value
        )
    )
    val uiState: StateFlow<RouteUiState> = _uiState.asStateFlow()

    fun selectRouteType(type: RouteType) {
        _uiState.value = _uiState.value.copy(selectedRouteType = type)
    }

    fun setDestination(destination: String) {
        _uiState.value = _uiState.value.copy(selectedDestination = destination)
    }

    fun setStartLocation(location: String) {
        _uiState.value = _uiState.value.copy(selectedStartLocation = location)
    }

    fun addWaypoint(name: String) {
        val newWp = Waypoint(
            id = "wp_${System.currentTimeMillis()}",
            name = name,
            latitude = 18.7000,
            longitude = 73.4000,
            isStopover = true
        )
        routeRepository.addWaypoint(newWp)
        _uiState.value = _uiState.value.copy(waypoints = routeRepository.waypoints.value)
    }

    fun removeWaypoint(id: String) {
        routeRepository.removeWaypoint(id)
        _uiState.value = _uiState.value.copy(waypoints = routeRepository.waypoints.value)
    }
}
