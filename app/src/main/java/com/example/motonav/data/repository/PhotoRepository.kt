package com.example.motonav.data.repository

import com.example.motonav.data.local.AppDatabase
import com.example.motonav.data.model.PhotoPost
import com.example.motonav.data.remote.ApiClient
import com.example.motonav.data.remote.MotoNavApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotoRepository(
    private val apiService: MotoNavApiService = ApiClient.apiService
) {

    private val _photoPosts = MutableStateFlow<List<PhotoPost>>(AppDatabase.photoPosts.value)
    val photoPosts: StateFlow<List<PhotoPost>> = _photoPosts.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshPhotos()
        }
    }

    suspend fun refreshPhotos() {
        try {
            val response = apiService.getPhotos()
            if (response.isSuccessful && response.body()?.data != null) {
                val posts = response.body()!!.data!!
                _photoPosts.value = posts
                AppDatabase.setPhotoPosts(posts)
            }
        } catch (_: Exception) {}
    }

    fun getPhotoById(id: String): PhotoPost? {
        return photoPosts.value.find { it.id == id }
    }

    suspend fun addPhotoPost(
        imageUri: String,
        caption: String,
        locationName: String,
        latitude: Double,
        longitude: Double,
        tags: List<String>
    ): PhotoPost {
        val newPost = PhotoPost(
            id = "photo_${System.currentTimeMillis()}",
            imageUri = imageUri,
            caption = caption,
            locationName = locationName,
            latitude = latitude,
            longitude = longitude,
            tags = tags,
            timestamp = "Just now",
            isUploadedRemote = false
        )
        AppDatabase.addPhotoPost(newPost)
        _photoPosts.value = AppDatabase.photoPosts.value

        return newPost
    }
}
