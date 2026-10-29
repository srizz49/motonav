package com.example.motonav

import com.example.motonav.data.model.RouteType
import com.example.motonav.data.repository.RouteRepository
import com.example.motonav.viewmodel.RouteViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RouteViewModelTest {

    private lateinit var repository: RouteRepository
    private lateinit var viewModel: RouteViewModel

    @Before
    fun setUp() {
        repository = RouteRepository()
        viewModel = RouteViewModel(repository)
    }

    @Test
    fun testSelectRouteType_updatesState() {
        viewModel.selectRouteType(RouteType.FASTEST)
        assertEquals(RouteType.FASTEST, viewModel.uiState.value.selectedRouteType)

        viewModel.selectRouteType(RouteType.SCENIC)
        assertEquals(RouteType.SCENIC, viewModel.uiState.value.selectedRouteType)
    }

    @Test
    fun testSetDestination_updatesState() {
        viewModel.setDestination("Lavasa Hill City")
        assertEquals("Lavasa Hill City", viewModel.uiState.value.selectedDestination)
    }

    @Test
    fun testAddAndRemoveWaypoint() {
        val initialCount = viewModel.uiState.value.waypoints.size
        viewModel.addWaypoint("Tea Pitstop")
        assertEquals(initialCount + 1, viewModel.uiState.value.waypoints.size)

        val addedWp = viewModel.uiState.value.waypoints.last()
        viewModel.removeWaypoint(addedWp.id)
        assertEquals(initialCount, viewModel.uiState.value.waypoints.size)
    }
}
