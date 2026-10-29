package com.example.motonav.data.repository

import com.example.motonav.data.local.AppDatabase
import com.example.motonav.data.model.RideHistory
import com.example.motonav.data.model.RouteType
import kotlinx.coroutines.flow.StateFlow

class RidesRepository {

    val rideHistory: StateFlow<List<RideHistory>> = AppDatabase.rideHistory

    fun saveCompletedRide(
        title: String,
        distanceKm: Double,
        durationMinutes: Int,
        fuelCostInr: Double,
        routeType: RouteType,
        bikeModel: String
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
        return newRide
    }
}
