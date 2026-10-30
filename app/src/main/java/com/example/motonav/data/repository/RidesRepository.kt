package com.example.motonav.data.repository

import com.example.motonav.data.local.AppDatabase
import com.example.motonav.data.model.RideHistory
import com.example.motonav.data.model.RouteType
import com.example.motonav.data.remote.ApiClient
import com.example.motonav.data.remote.MotoNavApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RidesRepository(
    private val apiService: MotoNavApiService = ApiClient.apiService
) {

    private val _rideHistory = MutableStateFlow<List<RideHistory>>(AppDatabase.rideHistory.value)
    val rideHistory: StateFlow<List<RideHistory>> = _rideHistory.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshRides()
        }
    }

    suspend fun refreshRides(userId: String = "user_1") {
        try {
            val response = apiService.getRideHistory(userId)
            if (response.isSuccessful && response.body()?.data != null) {
                val rides = response.body()!!.data!!
                _rideHistory.value = rides
                AppDatabase.setRideHistory(rides)
            }
        } catch (_: Exception) {}
    }

    suspend fun saveCompletedRide(
        title: String,
        distanceKm: Double,
        durationMinutes: Int,
        fuelCostInr: Double,
        routeType: RouteType,
        bikeModel: String,
        userId: String = "user_1"
    ): RideHistory {
        val newRide = RideHistory(
            id = "ride_${System.currentTimeMillis()}",
            title = title,
            date = "Today",
            distanceKm = distanceKm,
            durationMinutes = durationMinutes,
            avgSpeedKmh = if (durationMinutes > 0) (distanceKm / (durationMinutes / 60.0)) else 0.0,
            fuelCostInr = fuelCostInr,
            routeType = routeType,
            bikeModel = bikeModel
        )
        AppDatabase.addRideHistory(newRide)
        _rideHistory.value = AppDatabase.rideHistory.value

        try {
            val body = mapOf(
                "title" to title,
                "distanceKm" to distanceKm,
                "durationMinutes" to durationMinutes,
                "fuelCostInr" to fuelCostInr,
                "routeType" to routeType.name,
                "bikeModel" to bikeModel
            )
            apiService.recordRide(userId, body)
            refreshRides(userId)
        } catch (_: Exception) {}

        return newRide
    }
}
