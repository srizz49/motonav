package com.example.motonav.viewmodel

import androidx.lifecycle.ViewModel
import com.example.motonav.data.model.PhotoPost
import com.example.motonav.data.repository.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AddPhotoUiState(
    val selectedImageUri: String? = null,
    val caption: String = "",
    val locationName: String = "Lonavala Viewpoint",
    val latitude: Double = 18.7557,
    val longitude: Double = 73.4091,
    val tagsInput: String = "TwistyRoads, ScenicView",
    val isUploading: Boolean = false,
    val uploadSuccess: Boolean = false,
    val errorMessage: String? = null
)

class PhotoViewModel(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    val photoPosts: StateFlow<List<PhotoPost>> = photoRepository.photoPosts

    private val _addPhotoState = MutableStateFlow(AddPhotoUiState())
    val addPhotoState: StateFlow<AddPhotoUiState> = _addPhotoState.asStateFlow()

    fun onImageSelected(uriString: String) {
        _addPhotoState.value = _addPhotoState.value.copy(selectedImageUri = uriString, errorMessage = null)
    }

    fun onCaptionChanged(caption: String) {
        _addPhotoState.value = _addPhotoState.value.copy(caption = caption)
    }

    fun onLocationNameChanged(location: String) {
        _addPhotoState.value = _addPhotoState.value.copy(locationName = location)
    }

    fun onTagsChanged(tags: String) {
        _addPhotoState.value = _addPhotoState.value.copy(tagsInput = tags)
    }

    fun getPhotoById(id: String): PhotoPost? {
        return photoRepository.getPhotoById(id)
    }

    fun submitPhotoPost() {
        val state = _addPhotoState.value
        val uri = state.selectedImageUri ?: return
        if (uri.isBlank()) {
            _addPhotoState.value = state.copy(errorMessage = "Please select a photo first.")
            return
        }

        val parsedTags = state.tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        photoRepository.addPhotoPost(
            imageUri = uri,
            caption = state.caption,
            locationName = state.locationName,
            latitude = state.latitude,
            longitude = state.longitude,
            tags = parsedTags
        )
        _addPhotoState.value = AddPhotoUiState(uploadSuccess = true)
    }

    private fun String?.isNull_or_Empty_or_Blank(): Boolean = this.isNullOrBlank()
}
