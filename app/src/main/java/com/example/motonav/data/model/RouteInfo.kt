package com.example.motonav.data.model

enum class RouteType {
    SCENIC,
    FASTEST
}

data class RouteInfo(
    val id: String,
    val title: String,
    val startLocationName: String,
    val destinationName: String,
    val type: RouteType,
    val distanceKm: Double,
    val durationMinutes: Int,
    val scenicScore: Int = 5, // out of 10
    val twistyScore: Int = 8, // out of 10
    val waypoints: List<Waypoint> = emptyList(),
    val estimatedFuelCostInr: Double = 0.0,
    val roadConditions: String = "Good asphalt, scenic mountain curves",
    val description: String = ""
)
