package com.example.motonav.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motonav.data.model.RouteInfo
import com.example.motonav.data.model.ScenicPlace
import com.example.motonav.data.repository.ExploreRepository
import com.example.motonav.data.repository.RouteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val startLocation: String = "Current Location (Pune)",
    val destinationQuery: String = "",
    val weatherCondition: String = "24°C • Perfect Riding Weather",
    val recommendedRoute: RouteInfo? = null,
    val nearbyPlaces: List<ScenicPlace> = emptyList(),
    val isSearching: Boolean = false,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val routeRepository: RouteRepository,
    private val exploreRepository: ExploreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            recommendedRoute = routeRepository.scenicRoute.value,
            nearbyPlaces = exploreRepository.scenicPlaces.value
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            routeRepository.scenicRoute.collect { route ->
                _uiState.value = _uiState.value.copy(recommendedRoute = route)
            }
        }
        viewModelScope.launch {
            exploreRepository.scenicPlaces.collect { places ->
                _uiState.value = _uiState.value.copy(nearbyPlaces = places)
            }
        }
    }

    fun onDestinationQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(destinationQuery = query)
    }

    fun onStartLocationChanged(location: String) {
        _uiState.value = _uiState.value.copy(startLocation = location)
    }
}
