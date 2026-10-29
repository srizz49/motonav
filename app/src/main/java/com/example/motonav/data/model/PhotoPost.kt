package com.example.motonav.data.model

data class PhotoPost(
    val id: String,
    val imageUri: String,
    val caption: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val tags: List<String> = emptyList(),
    val timestamp: String,
    val isUploadedRemote: Boolean = false,
    val uploaderName: String = "Alex Rider"
)
