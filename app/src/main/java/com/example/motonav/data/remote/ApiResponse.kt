package com.example.motonav.data.remote

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class RouteResponse(
    val scenic: com.example.motonav.data.model.RouteInfo,
    val fastest: com.example.motonav.data.model.RouteInfo
)

data class FuelStationDto(
    val id: String,
    val name: String,
    val brand: String,
    val address: String,
    val distanceKm: Double,
    val isOpen24Hours: Boolean = true,
    val hasAirWater: Boolean = true
)

data class FuelResponse(
    val petrolPrice: Double,
    val stations: List<FuelStationDto>
)

data class ToggleSaveResponse(
    val isSaved: Boolean
)

