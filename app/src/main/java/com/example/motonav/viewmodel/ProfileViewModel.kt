package com.example.motonav.viewmodel

import androidx.lifecycle.ViewModel
import com.example.motonav.data.model.Bike
import com.example.motonav.data.model.User
import com.example.motonav.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AddBikeUiState(
    val makeInput: String = "",
    val modelInput: String = "",
    val yearInput: String = "2024",
    val mileageInput: String = "30.0",
    val tankCapacityInput: String = "15.0",
    val isDefault: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val user: StateFlow<User> = profileRepository.user
    val bikes: StateFlow<List<Bike>> = profileRepository.bikes

    private val _addBikeState = MutableStateFlow(AddBikeUiState())
    val addBikeState: StateFlow<AddBikeUiState> = _addBikeState.asStateFlow()

    fun onMakeChanged(make: String) { _addBikeState.value = _addBikeState.value.copy(makeInput = make) }
    fun onModelChanged(model: String) { _addBikeState.value = _addBikeState.value.copy(modelInput = model) }
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
        profileRepository.addBike(newBike)
        _addBikeState.value = AddBikeUiState()
    }

    fun deleteBike(bikeId: String) {
        profileRepository.deleteBike(bikeId)
    }

    fun updateBio(bio: String) {
        profileRepository.updateUserBio(bio)
    }
}
