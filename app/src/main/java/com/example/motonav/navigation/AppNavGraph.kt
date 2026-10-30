package com.example.motonav.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.motonav.di.AppModule
import com.example.motonav.ui.screens.auth.LoginScreen
import com.example.motonav.ui.screens.auth.SignUpScreen
import com.example.motonav.ui.screens.bike.BikeGarageScreen
import com.example.motonav.ui.screens.explore.ExploreScreen
import com.example.motonav.ui.screens.explore.ScenicPlaceDetailsScreen
import com.example.motonav.ui.screens.fuel.FuelCalculatorScreen
import com.example.motonav.ui.screens.fuel.FuelCostResultsScreen
import com.example.motonav.ui.screens.fuel.FuelStationsScreen
import com.example.motonav.ui.screens.home.HomeScreen
import com.example.motonav.ui.screens.map.MapScreen
import com.example.motonav.ui.screens.navigation.ActiveNavigationScreen
import com.example.motonav.ui.screens.navigation.NavigationDetailsScreen
import com.example.motonav.ui.screens.onboarding.OnboardingScreen
import com.example.motonav.ui.screens.permissions.PermissionsScreen
import com.example.motonav.ui.screens.photos.AddPhotoScreen
import com.example.motonav.ui.screens.photos.PhotoDetailsScreen
import com.example.motonav.ui.screens.photos.PhotoMapScreen
import com.example.motonav.ui.screens.profile.RiderProfileScreen
import com.example.motonav.ui.screens.rides.MyRidesScreen
import com.example.motonav.ui.screens.route.FastestRouteDetailsScreen
import com.example.motonav.ui.screens.route.LocationSelectionScreen
import com.example.motonav.ui.screens.route.PreRideSummaryScreen
import com.example.motonav.ui.screens.route.RouteComparisonScreen
import com.example.motonav.ui.screens.route.RouteStopsScreen
import com.example.motonav.ui.screens.route.ScenicRouteDetailsScreen
import com.example.motonav.ui.screens.saved.SavedPlacesScreen
import com.example.motonav.ui.screens.search.DestinationSearchScreen
import com.example.motonav.ui.screens.settings.SettingsScreen
import com.example.motonav.ui.screens.splash.SplashScreen
import com.example.motonav.ui.screens.welcome.WelcomeScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    val authViewModel = AppModule.provideAuthViewModel()
    val homeViewModel = AppModule.provideHomeViewModel()
    val routeViewModel = AppModule.provideRouteViewModel()
    val navigationViewModel = AppModule.provideNavigationViewModel()
    val fuelViewModel = AppModule.provideFuelViewModel()
    val photoViewModel = AppModule.providePhotoViewModel()
    val exploreViewModel = AppModule.provideExploreViewModel()
    val ridesViewModel = AppModule.provideRidesViewModel()
    val profileViewModel = AppModule.provideProfileViewModel()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Splash
            composable(Screen.Splash.route) {
                SplashScreen(navController = navController)
            }

            // 2. Welcome
            composable(Screen.Welcome.route) {
                WelcomeScreen(navController = navController)
            }

            // 3. Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(navController = navController)
            }

            // 4. SignUp
            composable(Screen.SignUp.route) {
                SignUpScreen(navController = navController, viewModel = authViewModel)
            }

            // 5. Login
            composable(Screen.Login.route) {
                LoginScreen(navController = navController, viewModel = authViewModel)
            }

            // 6. Permissions
            composable(Screen.Permissions.route) {
                PermissionsScreen(navController = navController)
            }

            // 7. Home
            composable(Screen.Home.route) {
                HomeScreen(navController = navController, viewModel = homeViewModel)
            }

            // 8. Map
            composable(Screen.Map.route) {
                MapScreen(navController = navController)
            }

            // 9. Destination Search
            composable(Screen.DestinationSearch.route) {
                DestinationSearchScreen(navController = navController, viewModel = routeViewModel)
            }

            // 10. Location Selection
            composable(Screen.LocationSelection.route) {
                LocationSelectionScreen(navController = navController, viewModel = routeViewModel)
            }

            // 11. Route Comparison
            composable(Screen.RouteComparison.route) {
                RouteComparisonScreen(navController = navController, viewModel = routeViewModel)
            }

            // 12. Scenic Route Details
            composable(Screen.ScenicRouteDetails.route) {
                ScenicRouteDetailsScreen(navController = navController, viewModel = routeViewModel)
            }

            // 13. Fastest Route Details
            composable(Screen.FastestRouteDetails.route) {
                FastestRouteDetailsScreen(navController = navController, viewModel = routeViewModel)
            }

            // 14. Route Stops
            composable(Screen.RouteStops.route) {
                RouteStopsScreen(navController = navController, viewModel = routeViewModel)
            }

            // 15. Pre Ride Summary
            composable(Screen.PreRideSummary.route) {
                PreRideSummaryScreen(navController = navController, viewModel = routeViewModel)
            }

            // 16. Active Navigation
            composable(Screen.ActiveNavigation.route) {
                ActiveNavigationScreen(navController = navController, viewModel = navigationViewModel)
            }

            // 17. Navigation Details
            composable(Screen.NavigationDetails.route) {
                NavigationDetailsScreen(navController = navController)
            }

            // 18. Fuel Stations
            composable(Screen.FuelStations.route) {
                FuelStationsScreen(navController = navController, viewModel = fuelViewModel)
            }

            // 19. Fuel Calculator
            composable(Screen.FuelCalculator.route) {
                FuelCalculatorScreen(navController = navController, viewModel = fuelViewModel)
            }

            // 20. Fuel Cost Results
            composable(Screen.FuelCostResults.route) {
                FuelCostResultsScreen(navController = navController, viewModel = fuelViewModel)
            }

            // 21. Bike Garage
            composable(Screen.BikeGarage.route) {
                BikeGarageScreen(navController = navController, viewModel = profileViewModel)
            }

            // 22. Explore
            composable(Screen.Explore.route) {
                ExploreScreen(navController = navController, viewModel = exploreViewModel)
            }

            // 23. Photo Map
            composable(Screen.PhotoMap.route) {
                PhotoMapScreen(navController = navController, viewModel = photoViewModel)
            }

            // 24. Add Photo
            composable(Screen.AddPhoto.route) {
                AddPhotoScreen(navController = navController, viewModel = photoViewModel)
            }

            // 25. Photo Details
            composable(
                route = Screen.PhotoDetails.route,
                arguments = listOf(navArgument("photoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val photoId = backStackEntry.arguments?.getString("photoId") ?: "photo_1"
                PhotoDetailsScreen(navController = navController, viewModel = photoViewModel, photoId = photoId)
            }

            // 26. Scenic Place Details
            composable(
                route = Screen.ScenicPlaceDetails.route,
                arguments = listOf(navArgument("placeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val placeId = backStackEntry.arguments?.getString("placeId") ?: "place_1"
                ScenicPlaceDetailsScreen(navController = navController, viewModel = exploreViewModel, placeId = placeId)
            }

            // 27. Saved Places
            composable(Screen.SavedPlaces.route) {
                SavedPlacesScreen(navController = navController, viewModel = exploreViewModel)
            }

            // 28. My Rides
            composable(Screen.MyRides.route) {
                MyRidesScreen(navController = navController, viewModel = ridesViewModel)
            }

            // 29. Rider Profile
            composable(Screen.RiderProfile.route) {
                RiderProfileScreen(
                    navController = navController,
                    profileViewModel = profileViewModel,
                    authViewModel = authViewModel
                )
            }

            // 30. Settings
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
        }
    }
}
