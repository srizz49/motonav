package com.example.motonav.data.model

data class Bike(
    val id: String,
    val make: String,
    val model: String,
    val year: Int? = null,
    val mileageKmpl: Double, // Fuel efficiency in km/l
    val fuelTankCapacityLitres: Double = 15.0,
    val isDefault: Boolean = false
)
