package com.example.motonav.data.model

data class RideHistory(
    val id: String,
    val title: String,
    val date: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val avgSpeedKmh: Double,
    val fuelCostInr: Double,
    val routeType: RouteType,
    val bikeModel: String
)
