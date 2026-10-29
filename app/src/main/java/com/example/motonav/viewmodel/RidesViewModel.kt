package com.example.motonav.viewmodel

import androidx.lifecycle.ViewModel
import com.example.motonav.data.model.RideHistory
import com.example.motonav.data.repository.RidesRepository
import kotlinx.coroutines.flow.StateFlow

class RidesViewModel(
    private val ridesRepository: RidesRepository
) : ViewModel() {

    val rideHistory: StateFlow<List<RideHistory>> = ridesRepository.rideHistory
}
