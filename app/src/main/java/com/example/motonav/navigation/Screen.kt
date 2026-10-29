package com.example.motonav.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Onboarding : Screen("onboarding")
    object SignUp : Screen("signup")
    object Login : Screen("login")
    object Permissions : Screen("permissions")

    // Bottom bar screens
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Explore : Screen("explore", "Explore", Icons.Default.Explore)
    object AddPhoto : Screen("add_photo", "Add Photo", Icons.Default.AddAPhoto)
    object MyRides : Screen("my_rides", "My Rides", Icons.Default.TwoWheeler)
    object RiderProfile : Screen("rider_profile", "Profile", Icons.Default.Person)

    // Maps & Routes
    object Map : Screen("map")
    object DestinationSearch : Screen("destination_search")
    object LocationSelection : Screen("location_selection")
    object RouteComparison : Screen("route_comparison")
    object ScenicRouteDetails : Screen("scenic_route_details")
    object FastestRouteDetails : Screen("fastest_route_details")
    object RouteStops : Screen("route_stops")
    object PreRideSummary : Screen("pre_ride_summary")

    // Active Navigation
    object ActiveNavigation : Screen("active_navigation")
    object NavigationDetails : Screen("navigation_details")

    // Fuel & Bike
    object FuelStations : Screen("fuel_stations")
    object FuelCalculator : Screen("fuel_calculator")
    object FuelCostResults : Screen("fuel_cost_results")
    object BikeGarage : Screen("bike_garage")

    // Photo & Explore details
    object PhotoMap : Screen("photo_map")
    object PhotoDetails : Screen("photo_details/{photoId}") {
        fun createRoute(photoId: String) = "photo_details/$photoId"
    }
    object ScenicPlaceDetails : Screen("scenic_place_details/{placeId}") {
        fun createRoute(placeId: String) = "scenic_place_details/$placeId"
    }
    object SavedPlaces : Screen("saved_places")
    object Settings : Screen("settings")

    companion object {
        val bottomNavItems = listOf(
            Home,
            Explore,
            AddPhoto,
            MyRides,
            RiderProfile
        )
    }
}
