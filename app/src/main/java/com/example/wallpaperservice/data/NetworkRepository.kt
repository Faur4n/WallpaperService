package com.example.wallpaperservice.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NetworkRepository(private val api : ImageApiService) {
     val imageData = MutableLiveData<ImageResponse>()

    suspend fun getImage() {
            imageData.postValue(api.getTodayImage())
    }

    suspend fun getImageByDate(date: String) {
        imageData.postValue(api.getImageByDate(date))
    }

    //Singleton with kotlin
    companion object{
        @Volatile private var instance: NetworkRepository? = null
        private val LOCK = Any()

        operator fun invoke() = instance ?: synchronized(LOCK) {
            instance ?: NetworkRepository(ImageApiService.invoke()).also { instance = it }
        }
    }
}