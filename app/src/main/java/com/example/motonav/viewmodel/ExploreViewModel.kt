package com.example.motonav.viewmodel

import androidx.lifecycle.ViewModel
import com.example.motonav.data.model.ScenicPlace
import com.example.motonav.data.repository.ExploreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ExploreUiState(
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val categories: List<String> = listOf("All", "Mountain Pass", "Viewpoint", "Waterfall Pass", "Biker Pitstop"),
    val scenicPlaces: List<ScenicPlace> = emptyList()
)

class ExploreViewModel(
    private val exploreRepository: ExploreRepository
) : ViewModel() {

    val allScenicPlaces: StateFlow<List<ScenicPlace>> = exploreRepository.scenicPlaces

    private val _uiState = MutableStateFlow(
        ExploreUiState(scenicPlaces = exploreRepository.scenicPlaces.value)
    )
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterPlaces()
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        filterPlaces()
    }

    fun toggleSavePlace(placeId: String) {
        exploreRepository.toggleSavePlace(placeId)
    }

    fun getPlaceById(id: String): ScenicPlace? {
        return exploreRepository.getPlaceById(id)
    }

    private fun filterPlaces() {
        val query = _uiState.value.searchQuery.lowercase()
        val cat = _uiState.value.selectedCategory
        val list = allScenicPlaces.value.filter { place ->
            val matchesCat = (cat == "All" || place.category.equals(cat, ignoreCase = true))
            val matchesQuery = (query.isBlank() || place.name.lowercase().contains(query) || place.description.lowercase().contains(query))
            matchesCat && matchesQuery
        }
        _uiState.value = _uiState.value.copy(scenicPlaces = list)
    }
}
