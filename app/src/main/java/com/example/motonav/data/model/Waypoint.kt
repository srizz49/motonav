package com.example.motonav.data.model

data class Waypoint(
    val id: String,
    val name: String,
    val address: String = "",
    val latitude: Double,
    val longitude: Double,
    val isStopover: Boolean = false,
    val isScenicViewpoint: Boolean = false
)
