package com.example.wallpaperservice.ui.main

import android.media.Image
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallpaperservice.data.ImageApiService
import com.example.wallpaperservice.data.ImageResponse
import com.example.wallpaperservice.data.NetworkRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val repository : NetworkRepository = NetworkRepository(ImageApiService())
    val image : MutableLiveData<ImageResponse>
    init {
        image = repository.imageData
    }
    fun requestImage() = viewModelScope.launch{
        repository.getImage()
    }

    fun requestImageByDate(date: String) = viewModelScope.launch{
        repository.getImageByDate(date)
    }
}
