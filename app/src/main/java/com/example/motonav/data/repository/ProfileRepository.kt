package com.example.motonav.data.repository

import com.example.motonav.data.local.AppDatabase
import com.example.motonav.data.model.Bike
import com.example.motonav.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileRepository {

    private val _user = MutableStateFlow(
        User(
            id = "rider_001",
            name = "Alex Rider",
            email = "alex.rider@motonav.com",
            bio = "Twisty road connoisseur & long-distance motorcycle touring enthusiast.",
            totalRidesCount = 28,
            totalDistanceKm = 3450.0
        )
    )
    val user: StateFlow<User> = _user.asStateFlow()

    val bikes: StateFlow<List<Bike>> = AppDatabase.bikes

    fun addBike(bike: Bike) {
        AppDatabase.addBike(bike)
    }

    fun deleteBike(bikeId: String) {
        AppDatabase.deleteBike(bikeId)
    }

    fun updateUserBio(bio: String) {
        _user.value = _user.value.copy(bio = bio)
    }
}
