package com.example.wallpaperservice.services.works

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.wallpaperservice.services.BackgroundService

class BackgroundWorker(private val appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Do the work here--in this case, upload the images.

        startService()

        // Indicate whether the task finished successfully with the Result
        return Result.success()
    }

    private fun startService() {
        val serviceIntent = Intent(appContext, BackgroundService::class.java)
        appContext.startService(serviceIntent)
    }
}