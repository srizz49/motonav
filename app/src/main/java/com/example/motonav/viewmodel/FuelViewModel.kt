package com.example.motonav.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motonav.data.model.Bike
import com.example.motonav.data.model.FuelEstimate
import com.example.motonav.data.repository.FuelRepository
import com.example.motonav.data.repository.FuelStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FuelUiState(
    val distanceInput: String = "142.5",
    val mileageInput: String = "30.0",
    val priceInput: String = "102.50",
    val selectedBike: Bike? = null,
    val availableBikes: List<Bike> = emptyList(),
    val estimate: FuelEstimate? = null,
    val scenicRouteCostInr: Double = 486.8,
    val fastestRouteCostInr: Double = 410.0,
    val isManualPrice: Boolean = true,
    val errorMessage: String? = null,
    val nearbyStations: List<FuelStation> = emptyList(),
    val isLoading: Boolean = false
)

class FuelViewModel(
    private val fuelRepository: FuelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FuelUiState(nearbyStations = fuelRepository.fuelStations.value))
    val uiState: StateFlow<FuelUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fuelRepository.bikes.collect { bikesList ->
                val defaultB = bikesList.find { it.isDefault } ?: bikesList.firstOrNull()
                _uiState.value = _uiState.value.copy(
                    availableBikes = bikesList,
                    selectedBike = defaultB,
                    mileageInput = defaultB?.mileageKmpl?.toString() ?: "30.0"
                )
                calculateFuel()
            }
        }
        viewModelScope.launch {
            fuelRepository.fuelStations.collect { stations ->
                _uiState.value = _uiState.value.copy(nearbyStations = stations)
            }
        }
        viewModelScope.launch {
            fuelRepository.petrolPrice.collect { price ->
                if (_uiState.value.isManualPrice) {
                    _uiState.value = _uiState.value.copy(priceInput = String.format("%.2f", price))
                    calculateFuel()
                }
            }
        }
    }

    fun refreshStations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            fuelRepository.refreshFuelData()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun onDistanceChanged(value: String) {
        _uiState.value = _uiState.value.copy(distanceInput = value)
        calculateFuel()
    }

    fun onMileageChanged(value: String) {
        _uiState.value = _uiState.value.copy(mileageInput = value)
        calculateFuel()
    }

    fun onPriceChanged(value: String) {
        _uiState.value = _uiState.value.copy(priceInput = value, isManualPrice = true)
        calculateFuel()
    }

    fun selectBike(bike: Bike) {
        _uiState.value = _uiState.value.copy(
            selectedBike = bike,
            mileageInput = bike.mileageKmpl.toString()
        )
        calculateFuel()
    }

    fun calculateFuel() {
        val dist = _uiState.value.distanceInput.toDoubleOrNull()
        val mlg = _uiState.value.mileageInput.toDoubleOrNull()
        val prc = _uiState.value.priceInput.toDoubleOrNull()

        if (dist == null || dist < 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "Distance must be a positive number")
            return
        }
        if (mlg == null || mlg <= 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "Mileage must be greater than zero")
            return
        }
        if (prc == null || prc < 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "Price must be a positive number")
            return
        }

        val bikeName = _uiState.value.selectedBike?.let { "${it.make} ${it.model}" } ?: "Selected Motorcycle"
        val est = fuelRepository.calculateFuelEstimate(
            distanceKm = dist,
            mileageKmpl = mlg,
            pricePerLitreInr = prc,
            bikeModel = bikeName,
            isManualPrice = _uiState.value.isManualPrice
        )

        val scenicCost = (142.5 / mlg) * prc
        val fastestCost = (120.0 / mlg) * prc

        _uiState.value = _uiState.value.copy(
            estimate = est,
            scenicRouteCostInr = (scenicCost * 10).toInt() / 10.0,
            fastestRouteCostInr = (fastestCost * 10).toInt() / 10.0,
            errorMessage = null
        )
    }
}
