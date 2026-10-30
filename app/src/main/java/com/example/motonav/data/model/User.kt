package com.example.motonav.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUri: String? = null,
    val bio: String = "MotoNav Rider",
    val memberSince: String = "May 2025",
    val totalRidesCount: Int = 0,
    val totalDistanceKm: Double = 0.0
)
