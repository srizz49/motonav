package com.example.motonav.data.repository

import com.example.motonav.data.local.AppDatabase
import com.example.motonav.data.model.ScenicPlace
import com.example.motonav.data.remote.ApiClient
import com.example.motonav.data.remote.MotoNavApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreRepository(
    private val apiService: MotoNavApiService = ApiClient.apiService
) {

    private val _scenicPlaces = MutableStateFlow<List<ScenicPlace>>(AppDatabase.scenicPlaces.value)
    val scenicPlaces: StateFlow<List<ScenicPlace>> = _scenicPlaces.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshPlaces()
        }
    }

    suspend fun refreshPlaces(userId: String = "user_1", category: String = "All", query: String = "") {
        try {
            val response = apiService.getScenicPlaces(userId, category, query)
            if (response.isSuccessful && response.body()?.data != null) {
                val places = response.body()!!.data!!
                _scenicPlaces.value = places
                AppDatabase.setScenicPlaces(places)
            }
        } catch (_: Exception) {
            // Keep local cached places
        }
    }

    fun getPlaceById(id: String): ScenicPlace? {
        return scenicPlaces.value.find { it.id == id }
    }

    suspend fun toggleSavePlace(placeId: String, userId: String = "user_1") {
        AppDatabase.toggleSavePlace(placeId)
        _scenicPlaces.value = _scenicPlaces.value.map {
            if (it.id == placeId) it.copy(isSaved = !it.isSaved) else it
        }

        try {
            apiService.toggleSavePlace(mapOf("userId" to userId, "placeId" to placeId))
        } catch (_: Exception) {}
    }
}
