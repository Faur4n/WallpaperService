package com.example.wallpaperservice.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.wallpaperservice.R
import com.example.wallpaperservice.data.ImageApiService
import com.example.wallpaperservice.data.ImageResponse
import com.example.wallpaperservice.data.NetworkRepository
import com.example.wallpaperservice.ui.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BackgroundService : Service(){

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var image: ImageResponse? = null
        Toast.makeText(this,"START",Toast.LENGTH_SHORT).show()

        val job = CoroutineScope(Dispatchers.IO).launch{
            image = ImageApiService().getTodayImage()
            if(image!!.mediaType != "image"){
                image = ImageApiService().getImageByDate("2020-04-23")
            }
        }

         val  notificationManager =
            getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, issueNotification())

        job.invokeOnCompletion {
            image?.let { it1 -> setWallpaper(it1) }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun setWallpaper(image: ImageResponse){
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        Glide.with(this)
            .asBitmap()
            .load(image.url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    try {
                        wallpaperManager.setBitmap(resource)
                        //Toast.makeText(this@BackgroundService,"DONE",Toast.LENGTH_SHORT).show()
                        stopSelf()

                    } catch (th: Throwable) {
                        //Cannot SetWallpaper Notification
                        Toast.makeText(this@BackgroundService,"ERROR",Toast.LENGTH_SHORT).show()
                        stopSelf()

                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    ////////
                }

            })
    }

    private fun issueNotification() : Notification?
    {
        // make the channel. The method has been discussed before.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel("CHANNEL_1", "Example channel", NotificationManager.IMPORTANCE_DEFAULT);
        }
        // the check ensures that the channel will only be made
        // if the device is running Android 8+

        val notification =
            NotificationCompat.Builder(this, "CHANNEL_1");
        // the second parameter is the channel id.
        // it should be the same as passed to the makeNotificationChannel() method

        notification.setSmallIcon(R.mipmap.ic_launcher) // can use any other icon
            .setContentTitle("Setup Wallpaper!")
            .setNumber(3).setAutoCancel(false)
            .setProgress(0,0,true).color = resources.getColor(R.color.colorPrimaryDark,theme)
        // this shows a number in the notification dots


        return notification.build()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun makeNotificationChannel(id : String,  name : String,  importance : Int)
    {
        val channel = NotificationChannel(id, name, importance);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

        val notificationManager = getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel);
    }

    override fun onDestroy() {
        super.onDestroy()
       //Toast.makeText(this,"DIE",Toast.LENGTH_SHORT).show()
        val  notificationManager =
            getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }


}