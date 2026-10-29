package com.example.motonav.data.local

import com.example.motonav.data.model.Bike
import com.example.motonav.data.model.PhotoPost
import com.example.motonav.data.model.RideHistory
import com.example.motonav.data.model.RouteInfo
import com.example.motonav.data.model.RouteType
import com.example.motonav.data.model.ScenicPlace
import com.example.motonav.data.model.Waypoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppDatabase {

    private val initialBikes = listOf(
        Bike("bike_1", "Royal Enfield", "Himalayan 450", 2024, 30.0, 17.0, isDefault = true),
        Bike("bike_2", "KTM", "390 Adventure", 2023, 28.5, 14.5, isDefault = false),
        Bike("bike_3", "BMW", "G 310 GS", 2023, 32.0, 11.5, isDefault = false)
    )

    private val initialScenicPlaces = listOf(
        ScenicPlace(
            id = "place_1",
            name = "Western Ghats Twisty Loop",
            category = "Mountain Pass",
            description = "Sweeping curves, hairpin turns, and dense fog during monsoons. Premier motorcycle destination.",
            imageUrl = "https://picsum.photos/id/1018/800/400",
            rating = 4.9,
            reviewCount = 312,
            distanceKm = 48.0,
            latitude = 18.5204,
            longitude = 73.8567,
            isSaved = true
        ),
        ScenicPlace(
            id = "place_2",
            name = "Malshej Ghat Viewpoint",
            category = "Viewpoint",
            description = "Waterfall cascading directly onto the mountain road. Epic photography spots.",
            imageUrl = "https://picsum.photos/id/1015/800/400",
            rating = 4.8,
            reviewCount = 245,
            distanceKm = 85.0,
            latitude = 19.3400,
            longitude = 73.7800,
            isSaved = true
        ),
        ScenicPlace(
            id = "place_3",
            name = "Tamhini Ghat Canyon",
            category = "Waterfall Pass",
            description = "Lush valley road with cliffside views and continuous winding tarmac.",
            imageUrl = "https://picsum.photos/id/1043/800/400",
            rating = 4.7,
            reviewCount = 189,
            distanceKm = 62.0,
            latitude = 18.4700,
            longitude = 73.4300,
            isSaved = false
        ),
        ScenicPlace(
            id = "place_4",
            name = "Valley Edge Pitstop",
            category = "Biker Pitstop",
            description = "Famous tea and snack joint popular among morning Sunday riders.",
            imageUrl = "https://picsum.photos/id/1060/800/400",
            rating = 4.6,
            reviewCount = 98,
            distanceKm = 34.0,
            latitude = 18.6000,
            longitude = 73.7000,
            isSaved = false
        )
    )

    private val initialPhotoPosts = listOf(
        PhotoPost(
            id = "photo_1",
            imageUri = "https://picsum.photos/id/1018/600/600",
            caption = "Early morning mist over Western Ghats curves!",
            locationName = "Western Ghats Loop",
            latitude = 18.5204,
            longitude = 73.8567,
            tags = listOf("Himalayan450", "TwistyRoads", "MorningRide"),
            timestamp = "2 days ago",
            isUploadedRemote = true
        ),
        PhotoPost(
            id = "photo_2",
            imageUri = "https://picsum.photos/id/1015/600/600",
            caption = "Chasing waterfalls at Malshej Pass.",
            locationName = "Malshej Viewpoint",
            latitude = 19.3400,
            longitude = 73.7800,
            tags = listOf("WaterfallRide", "AdventureBikes"),
            timestamp = "1 week ago",
            isUploadedRemote = true
        )
    )

    private val initialRides = listOf(
        RideHistory(
            id = "ride_1",
            title = "Sunday Western Ghats Explorer",
            date = "Oct 24, 2024",
            distanceKm = 142.5,
            durationMinutes = 195,
            avgSpeedKmh = 43.8,
            fuelCostInr = 486.5,
            routeType = RouteType.SCENIC,
            bikeModel = "Royal Enfield Himalayan 450"
        ),
        RideHistory(
            id = "ride_2",
            title = "Coastal Highway Cruise",
            date = "Oct 12, 2024",
            distanceKm = 210.0,
            durationMinutes = 260,
            avgSpeedKmh = 48.4,
            fuelCostInr = 717.5,
            routeType = RouteType.FASTEST,
            bikeModel = "Royal Enfield Himalayan 450"
        )
    )

    private val _bikes = MutableStateFlow(initialBikes)
    val bikes: StateFlow<List<Bike>> = _bikes.asStateFlow()

    private val _scenicPlaces = MutableStateFlow(initialScenicPlaces)
    val scenicPlaces: StateFlow<List<ScenicPlace>> = _scenicPlaces.asStateFlow()

    private val _photoPosts = MutableStateFlow(initialPhotoPosts)
    val photoPosts: StateFlow<List<PhotoPost>> = _photoPosts.asStateFlow()

    private val _rideHistory = MutableStateFlow(initialRides)
    val rideHistory: StateFlow<List<RideHistory>> = _rideHistory.asStateFlow()

    fun addBike(bike: Bike) {
        val updated = _bikes.value.toMutableList()
        if (bike.isDefault) {
            for (i in updated.indices) {
                updated[i] = updated[i].copy(isDefault = false)
            }
        }
        updated.add(bike)
        _bikes.value = updated
    }

    fun deleteBike(bikeId: String) {
        _bikes.value = _bikes.value.filter { it.id != bikeId }
    }

    fun toggleSavePlace(placeId: String) {
        _bikes.value // touch
        _scenicPlaces.value = _scenicPlaces.value.map {
            if (it.id == placeId) it.copy(isSaved = !it.isSaved) else it
        }
    }

    fun addPhotoPost(post: PhotoPost) {
        _photoPosts.value = listOf(post) + _photoPosts.value
    }

    fun addRideHistory(ride: RideHistory) {
        _rideHistory.value = listOf(ride) + _rideHistory.value
    }
}
