package com.example.motonav.data.repository

import com.example.motonav.data.local.AppDatabase
import com.example.motonav.data.local.DataStoreManager
import com.example.motonav.data.model.Bike
import com.example.motonav.data.model.FuelEstimate
import com.example.motonav.data.remote.ApiClient
import com.example.motonav.data.remote.MotoNavApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    private val dataStoreManager: DataStoreManager,
    private val apiService: MotoNavApiService = ApiClient.apiService
) {
    val bikes: Flow<List<Bike>> = AppDatabase.bikes
    val petrolPrice: Flow<Double> = dataStoreManager.petrolPriceFlow

    private val defaultStations = listOf(
        FuelStation("fs_1", "Shell Highway Station", "Shell", "NH-48 Km 34", 3.2),
        FuelStation("fs_2", "HP Auto Energy Point", "HPCL", "Lonavala Bypass Rd", 8.5),
        FuelStation("fs_3", "Indian Oil Express Fuel", "IOCL", "Mountain Pass Junction", 14.1),
        FuelStation("fs_4", "BPCL Rider Oasis", "BPCL", "Ghat Exit Km 92", 22.0)
    )

    private val _fuelStations = MutableStateFlow(defaultStations)
    val fuelStations: StateFlow<List<FuelStation>> = _fuelStations.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshFuelData()
        }
    }

    suspend fun refreshFuelData() {
        try {
            val response = apiService.getFuelData()
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                dataStoreManager.setPetrolPrice(data.petrolPrice)
                _fuelStations.value = data.stations.map {
                    FuelStation(
                        id = it.id,
                        name = it.name,
                        brand = it.brand,
                        address = it.address,
                        distanceKm = it.distanceKm,
                        isOpen24Hours = it.isOpen24Hours,
                        hasAirWater = it.hasAirWater
                    )
                }
            }
        } catch (_: Exception) {}
    }

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
