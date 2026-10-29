package com.example.motonav.data.model

data class ScenicPlace(
    val id: String,
    val name: String,
    val category: String, // e.g. "Viewpoint", "Waterfall", "Mountain Pass", "Pitstop"
    val description: String,
    val imageUrl: String,
    val rating: Double,
    val reviewCount: Int,
    val distanceKm: Double,
    val latitude: Double,
    val longitude: Double,
    val isSaved: Boolean = false
)
