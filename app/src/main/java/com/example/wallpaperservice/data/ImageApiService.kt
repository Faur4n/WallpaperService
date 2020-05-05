package com.example.wallpaperservice.data

import androidx.lifecycle.LiveData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


const val API_KEY = "Lz20yu6rklgYMgl6SV04IL9PApqrLn59Izvfyanh"


interface ImageApiService {

    @GET("planetary/apod")
    suspend fun getTodayImage() : ImageResponse


    @GET("planetary/apod")
    suspend fun getImageByDate(@Query("date") date: String) : ImageResponse


    companion object{
        operator fun invoke() : ImageApiService{
            val requestInterceptor = Interceptor{ chain ->

                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }
            val interceptor =  HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(interceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.nasa.gov/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImageApiService::class.java)
        }
    }
}