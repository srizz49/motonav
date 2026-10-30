package com.example.motonav.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motonav.data.model.Bike
import com.example.motonav.data.model.User
import com.example.motonav.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddBikeUiState(
    val makeInput: String = "",
    val modelInput: String = "",
    val yearInput: String = "2024",
    val mileageInput: String = "30.0",
    val tankCapacityInput: String = "15.0",
    val isDefault: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val user: StateFlow<User> = profileRepository.user
    val bikes: StateFlow<List<Bike>> = profileRepository.bikes

    private val _addBikeState = MutableStateFlow(AddBikeUiState())
    val addBikeState: StateFlow<AddBikeUiState> = _addBikeState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            profileRepository.refreshProfile()
            profileRepository.refreshBikes()
        }
    }

    fun onMakeChanged(make: String) { _addBikeState.value = _addBikeState.value.copy(makeInput = make, errorMessage = null) }
    fun onModelChanged(model: String) { _addBikeState.value = _addBikeState.value.copy(modelInput = model, errorMessage = null) }
    fun onYearChanged(year: String) { _addBikeState.value = _addBikeState.value.copy(yearInput = year) }
    fun onMileageChanged(mileage: String) { _addBikeState.value = _addBikeState.value.copy(mileageInput = mileage) }
    fun onTankCapacityChanged(cap: String) { _addBikeState.value = _addBikeState.value.copy(tankCapacityInput = cap) }
    fun onDefaultToggled(isDef: Boolean) { _addBikeState.value = _addBikeState.value.copy(isDefault = isDef) }

    fun addBike() {
        val s = _addBikeState.value
        if (s.makeInput.isBlank() || s.modelInput.isBlank()) {
            _addBikeState.value = s.copy(errorMessage = "Make and Model are required.")
            return
        }
        val mileage = s.mileageInput.toDoubleOrNull() ?: 30.0
        val tank = s.tankCapacityInput.toDoubleOrNull() ?: 15.0
        val yr = s.yearInput.toIntOrNull()

        val newBike = Bike(
            id = "bike_${System.currentTimeMillis()}",
            make = s.makeInput.trim(),
            model = s.modelInput.trim(),
            year = yr,
            mileageKmpl = mileage,
            fuelTankCapacityLitres = tank,
            isDefault = s.isDefault
        )

        _addBikeState.value = s.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                profileRepository.addBike(newBike)
                _addBikeState.value = AddBikeUiState()
            } catch (e: Exception) {
                _addBikeState.value = s.copy(isLoading = false, errorMessage = "Failed to add bike")
            }
        }
    }

    fun deleteBike(bikeId: String) {
        viewModelScope.launch {
            profileRepository.deleteBike(bikeId)
        }
    }

    fun updateBio(bio: String) {
        viewModelScope.launch {
            profileRepository.updateUserBio(bio)
        }
    }
}
