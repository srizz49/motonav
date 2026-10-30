package com.example.motonav.data.repository

import com.example.motonav.data.model.RouteInfo
import com.example.motonav.data.model.RouteType
import com.example.motonav.data.model.Waypoint
import com.example.motonav.data.remote.ApiClient
import com.example.motonav.data.remote.MotoNavApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RouteRepository(
    private val apiService: MotoNavApiService = ApiClient.apiService
) {

    private val defaultWaypoints = listOf(
        Waypoint("wp_1", "Lonavala Viewpoint", "NH 48, Lonavala", 18.7557, 73.4091, isStopover = true, isScenicViewpoint = true),
        Waypoint("wp_2", "Khandala Hairpin Bend", "Ghat Section", 18.7610, 73.3762, isStopover = false, isScenicViewpoint = true),
        Waypoint("wp_3", "Tiger Point Refreshments", "Amby Valley Rd", 18.6942, 73.3812, isStopover = true, isScenicViewpoint = true)
    )

    private val _scenicRoute = MutableStateFlow(
        RouteInfo(
            id = "route_scenic",
            title = "Western Ghats Twisties & Waterfalls",
            startLocationName = "Pune City",
            destinationName = "Mahabaleshwar Peak",
            type = RouteType.SCENIC,
            distanceKm = 142.5,
            durationMinutes = 195,
            scenicScore = 9,
            twistyScore = 9,
            waypoints = defaultWaypoints,
            roadConditions = "Smooth mountain curves, 12 hairpin bends, lush scenic vistas",
            description = "Selected for maximum riding enjoyment with sweeping valley views and motorcycling hotspots."
        )
    )

    private val _fastestRoute = MutableStateFlow(
        RouteInfo(
            id = "route_fastest",
            title = "NH-48 Express Corridor",
            startLocationName = "Pune City",
            destinationName = "Mahabaleshwar Peak",
            type = RouteType.FASTEST,
            distanceKm = 120.0,
            durationMinutes = 145,
            scenicScore = 5,
            twistyScore = 4,
            waypoints = emptyList(),
            roadConditions = "4-lane dual carriageway highway, bypass roads",
            description = "Optimized for minimal transit time and straight highway cruising."
        )
    )

    val scenicRoute: StateFlow<RouteInfo> = _scenicRoute.asStateFlow()
    val fastestRoute: StateFlow<RouteInfo> = _fastestRoute.asStateFlow()

    private val _waypoints = MutableStateFlow(defaultWaypoints)
    val waypoints: StateFlow<List<Waypoint>> = _waypoints.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshRoutes()
        }
    }

    suspend fun refreshRoutes() {
        try {
            val response = apiService.getRoutes()
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                _scenicRoute.value = data.scenic
                _fastestRoute.value = data.fastest
                _waypoints.value = data.scenic.waypoints
            }
        } catch (_: Exception) {}
    }

    fun addWaypoint(waypoint: Waypoint) {
        _waypoints.value = _waypoints.value + waypoint
        updateScenicRouteWaypoints(_waypoints.value)
    }

    fun removeWaypoint(id: String) {
        _waypoints.value = _waypoints.value.filter { it.id != id }
        updateScenicRouteWaypoints(_waypoints.value)
    }

    fun reorderWaypoints(newList: List<Waypoint>) {
        _waypoints.value = newList
        updateScenicRouteWaypoints(newList)
    }

    private fun updateScenicRouteWaypoints(list: List<Waypoint>) {
        _scenicRoute.value = _scenicRoute.value.copy(waypoints = list)
    }
}
