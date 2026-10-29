package com.example.motonav.data.repository

import com.example.motonav.data.local.AppDatabase
import com.example.motonav.data.local.DataStoreManager
import com.example.motonav.data.model.Bike
import com.example.motonav.data.model.FuelEstimate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class FuelStation(
    val id: String,
    val name: String,
    val brand: String,
    val address: String,
    val distanceKm: Double,
    val isOpen24Hours: Boolean = true,
    val hasAirWater: Boolean = true
)

class FuelRepository(
    private val dataStoreManager: DataStoreManager
) {
    val bikes: Flow<List<Bike>> = AppDatabase.bikes
    val petrolPrice: Flow<Double> = dataStoreManager.petrolPriceFlow

    val nearbyFuelStations = listOf(
        FuelStation("fs_1", "Shell Highway Station", "Shell", "NH-48 Km 34", 3.2),
        FuelStation("fs_2", "HP Auto Energy Point", "HPCL", "Lonavala Bypass Rd", 8.5),
        FuelStation("fs_3", "Indian Oil Express Fuel", "IOCL", "Mountain Pass Junction", 14.1),
        FuelStation("fs_4", "BPCL Rider Oasis", "BPCL", "Ghat Exit Km 92", 22.0)
    )

    fun calculateFuelEstimate(
        distanceKm: Double,
        mileageKmpl: Double,
        pricePerLitreInr: Double,
        bikeModel: String = "Custom Bike",
        isManualPrice: Boolean = true
    ): FuelEstimate {
        val validDistance = if (distanceKm < 0) 0.0 else distanceKm
        val validMileage = if (mileageKmpl <= 0) 1.0 else mileageKmpl
        val validPrice = if (pricePerLitreInr < 0) 0.0 else pricePerLitreInr

        val source = if (isManualPrice) {
            FuelEstimate.PriceSourceType.MANUAL
        } else {
            FuelEstimate.PriceSourceType.SAMPLE_DATA
        }

        return FuelEstimate(
            distanceKm = validDistance,
            mileageKmpl = validMileage,
            pricePerLitreInr = validPrice,
            bikeModel = bikeModel,
            priceSource = source
        )
    }

    suspend fun saveCustomPetrolPrice(price: Double) {
        dataStoreManager.setPetrolPrice(price)
    }
}
