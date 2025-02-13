package com.hanpro.prographyproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.source.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoViewModel : ViewModel() {
    private val _latestPhotos = MutableStateFlow<List<PhotoDetail>>(emptyList())
    val latestPhotos: StateFlow<List<PhotoDetail>> = _latestPhotos

    private val _randomPhoto = MutableStateFlow<PhotoDetail?>(null)
    val randomPhoto: StateFlow<PhotoDetail?> = _randomPhoto

    fun loadLatestPhotos(page: Int = 1, perPage: Int = 10) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.unsplashApi.photoPages(page = page, perPage = perPage)
                _latestPhotos.value += response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadRandomPhoto() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.unsplashApi.getRandomPhoto()
                _randomPhoto.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadPhotoId(id: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.unsplashApi.getPhoto(id)
                _randomPhoto.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}