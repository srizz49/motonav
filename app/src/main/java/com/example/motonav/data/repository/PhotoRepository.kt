package com.example.motonav.data.repository

import com.example.motonav.data.local.AppDatabase
import com.example.motonav.data.model.PhotoPost
import kotlinx.coroutines.flow.StateFlow

class PhotoRepository {

    val photoPosts: StateFlow<List<PhotoPost>> = AppDatabase.photoPosts

    fun getPhotoById(id: String): PhotoPost? {
        return photoPosts.value.find { it.id == id }
    }

    fun addPhotoPost(
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
        return newPost
    }
}
