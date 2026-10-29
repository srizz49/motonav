package com.example.motonav.data.model

data class FuelEstimate(
    val distanceKm: Double,
    val mileageKmpl: Double,
    val pricePerLitreInr: Double,
    val bikeModel: String = "Generic Bike",
    val priceSource: PriceSourceType = PriceSourceType.MANUAL
) {
    enum class PriceSourceType {
        MANUAL,
        SAMPLE_DATA,
        VERIFIED_EXTERNAL
    }

    val fuelRequiredLitres: Double
        get() = if (mileageKmpl > 0) distanceKm / mileageKmpl else 0.0

    val oneWayCostInr: Double
        get() = fuelRequiredLitres * pricePerLitreInr

    val roundTripCostInr: Double
        get() = oneWayCostInr * 2.0
}
