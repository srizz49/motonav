package com.example.motonav.di

import android.content.Context
import com.example.motonav.data.local.DataStoreManager
import com.example.motonav.data.repository.AuthRepository
import com.example.motonav.data.repository.ExploreRepository
import com.example.motonav.data.repository.FuelRepository
import com.example.motonav.data.repository.PhotoRepository
import com.example.motonav.data.repository.ProfileRepository
import com.example.motonav.data.repository.RidesRepository
import com.example.motonav.data.repository.RouteRepository
import com.example.motonav.viewmodel.AuthViewModel
import com.example.motonav.viewmodel.ExploreViewModel
import com.example.motonav.viewmodel.FuelViewModel
import com.example.motonav.viewmodel.HomeViewModel
import com.example.motonav.viewmodel.NavigationViewModel
import com.example.motonav.viewmodel.PhotoViewModel
import com.example.motonav.viewmodel.ProfileViewModel
import com.example.motonav.viewmodel.RidesViewModel
import com.example.motonav.viewmodel.RouteViewModel

object AppModule {

    private var dataStoreManager: DataStoreManager? = null

    fun init(context: Context) {
        if (dataStoreManager == null) {
            dataStoreManager = DataStoreManager(context.applicationContext)
        }
    }

    private fun getDataStoreManager(): DataStoreManager {
        return dataStoreManager ?: error("AppModule not initialized. Call AppModule.init(context) first.")
    }

    val authRepository by lazy { AuthRepository(getDataStoreManager()) }
    val routeRepository by lazy { RouteRepository() }
    val fuelRepository by lazy { FuelRepository(getDataStoreManager()) }
    val photoRepository by lazy { PhotoRepository() }
    val exploreRepository by lazy { ExploreRepository() }
    val ridesRepository by lazy { RidesRepository() }
    val profileRepository by lazy { ProfileRepository() }

    fun provideAuthViewModel(): AuthViewModel = AuthViewModel(authRepository)
    fun provideHomeViewModel(): HomeViewModel = HomeViewModel(routeRepository, exploreRepository)
    fun provideRouteViewModel(): RouteViewModel = RouteViewModel(routeRepository)
    fun provideNavigationViewModel(): NavigationViewModel = NavigationViewModel()
    fun provideFuelViewModel(): FuelViewModel = FuelViewModel(fuelRepository)
    fun providePhotoViewModel(): PhotoViewModel = PhotoViewModel(photoRepository)
    fun provideExploreViewModel(): ExploreViewModel = ExploreViewModel(exploreRepository)
    fun provideRidesViewModel(): RidesViewModel = RidesViewModel(ridesRepository)
    fun provideProfileViewModel(): ProfileViewModel = ProfileViewModel(profileRepository)
}
