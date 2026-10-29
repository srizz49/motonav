package com.example.motonav.data.repository

import com.example.motonav.data.local.AppDatabase
import com.example.motonav.data.model.ScenicPlace
import kotlinx.coroutines.flow.StateFlow

class ExploreRepository {

    val scenicPlaces: StateFlow<List<ScenicPlace>> = AppDatabase.scenicPlaces

    fun getPlaceById(id: String): ScenicPlace? {
        return scenicPlaces.value.find { it.id == id }
    }

    fun toggleSavePlace(placeId: String) {
        AppDatabase.toggleSavePlace(placeId)
    }
}
