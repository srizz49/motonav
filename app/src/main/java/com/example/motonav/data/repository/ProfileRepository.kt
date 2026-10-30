package com.example.motonav.data.repository

import com.example.motonav.data.local.AppDatabase
import com.example.motonav.data.local.DataStoreManager
import com.example.motonav.data.model.Bike
import com.example.motonav.data.model.User
import com.example.motonav.data.remote.ApiClient
import com.example.motonav.data.remote.MotoNavApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileRepository(
    private val dataStoreManager: DataStoreManager? = null,
    private val apiService: MotoNavApiService = ApiClient.apiService
) {

    private val _user = MutableStateFlow(
        User(
            id = "user_1",
            name = "Alex Rider",
            email = "alex@motonav.com",
            bio = "Twisty road connoisseur & long-distance motorcycle touring enthusiast.",
            totalRidesCount = 28,
            totalDistanceKm = 3450.0
        )
    )
    val user: StateFlow<User> = _user.asStateFlow()

    private val _bikes = MutableStateFlow<List<Bike>>(AppDatabase.bikes.value)
    val bikes: StateFlow<List<Bike>> = _bikes.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val userId = dataStoreManager?.userIdFlow?.first() ?: "user_1"
            val userName = dataStoreManager?.userNameFlow?.first() ?: "Alex Rider"
            val userEmail = dataStoreManager?.userEmailFlow?.first() ?: "alex@motonav.com"
            _user.value = _user.value.copy(id = userId, name = userName, email = userEmail)

            refreshProfile(userId)
            refreshBikes(userId)
        }
    }

    fun setUser(user: User) {
        _user.value = user
    }

    suspend fun refreshProfile(userId: String = _user.value.id) {
        try {
            val response = apiService.getProfile(userId)
            if (response.isSuccessful && response.body()?.data != null) {
                _user.value = response.body()!!.data!!
            }
        } catch (_: Exception) {}
    }

    suspend fun refreshBikes(userId: String = _user.value.id) {
        try {
            val response = apiService.getBikes(userId)
            if (response.isSuccessful && response.body()?.data != null) {
                val fetchedBikes = response.body()!!.data!!
                _bikes.value = fetchedBikes
                AppDatabase.setBikes(fetchedBikes)
            }
        } catch (_: Exception) {}
    }

    suspend fun addBike(bike: Bike, userId: String = _user.value.id): Result<Bike> {
        AppDatabase.addBike(bike)
        _bikes.value = AppDatabase.bikes.value

        return try {
            val response = apiService.addBike(userId, bike)
            if (response.isSuccessful && response.body()?.data != null) {
                refreshBikes(userId)
                Result.success(response.body()!!.data!!)
            } else {
                Result.success(bike)
            }
        } catch (e: Exception) {
            Result.success(bike)
        }
    }

    suspend fun deleteBike(bikeId: String, userId: String = _user.value.id): Result<Unit> {
        AppDatabase.deleteBike(bikeId)
        _bikes.value = AppDatabase.bikes.value

        return try {
            apiService.deleteBike(userId, bikeId)
            refreshBikes(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    suspend fun updateUserBio(bio: String, userId: String = _user.value.id) {
        _user.value = _user.value.copy(bio = bio)
        try {
            apiService.updateProfile(mapOf("id" to userId, "bio" to bio))
        } catch (_: Exception) {}
    }
}
