package com.example.motonav.data.model

data class User(
    val id: String = "user_1",
    val name: String = "Alex Rider",
    val email: String = "alex@motonav.com",
    val profileImageUri: String? = null,
    val bio: String = "Avid motorcycle explorer & twisty road enthusiast.",
    val memberSince: String = "March 2024",
    val totalRidesCount: Int = 28,
    val totalDistanceKm: Double = 3450.0
)
