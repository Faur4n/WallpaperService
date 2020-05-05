package com.example.wallpaperservice.ui.main

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import android.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.wallpaperservice.R
import com.example.wallpaperservice.data.ImageResponse
import com.example.wallpaperservice.services.ReminderReceiver
import com.example.wallpaperservice.services.works.BackgroundWorker
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.concurrent.TimeUnit

class MainFragment : Fragment(), androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {

    companion object {
        fun newInstance() = MainFragment()

        private const val PREF = "settings"
        private const val SWITCH = "switch"

    }
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.i("MAIN_FRAGMENT","onCreateView")

        return inflater.inflate(R.layout.main_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i("MAIN_FRAGMENT","onActivityCreated")
        setupToolbar()
        setupSwitch()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.image.observe(viewLifecycleOwner, Observer {
                image -> image?.let { updateImage(it) }
        })
        //Starter Request
        if(savedInstanceState == null){
            try {
                viewModel.requestImage()
            }catch (th: Throwable){
                Toast.makeText(context,"ERROR", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun setupSwitch(){
        val pref = requireActivity().getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val editor = pref.edit()
        val switch = repeat_switch//Generic

        val constraints = Constraints.Builder()
            //.setRequiresDeviceIdle(true)
            .build()

        val backgroundRequest =
            PeriodicWorkRequestBuilder<BackgroundWorker>(20, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()


        switch.isChecked = pref.getBoolean(SWITCH,false)
        //Listener
        switch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                //context?.let { ReminderReceiver.startAlarm(it) }
                context?.let {
                    WorkManager.getInstance(it)
                        .enqueue(backgroundRequest)
                }

                editor.putBoolean(SWITCH,true)
                editor.apply()
            }else{
                //context?.let { ReminderReceiver.cancelAlarm(it) }
                context?.let {  WorkManager.getInstance(it).cancelAllWork() }

                editor.putBoolean(SWITCH,false)
                editor.apply()
            }
        }

    }

    private fun setupToolbar(){
        val toolbar = requireActivity().toolbar
//        toolbar.inflateMenu(R.menu.menu);
       toolbar.setOnMenuItemClickListener(this)
       toolbar.title = resources.getString(R.string.app_name)

    }

    private fun updateImage(image: ImageResponse) {
        val imageView = imageView

        if(image.mediaType == "image") {
            Glide.with(this)
                .load(image.url)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {

                        progressBar.visibility = View.GONE
                        return false
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(ColorDrawable(Color.BLACK))
                .into(imageView)
        }else{
            viewModel.requestImageByDate("2020-04-23")
        }
        activity?.actionBar?.title = image.title
    }
    //Reload Request
    override fun onMenuItemClick(item: MenuItem): Boolean {
        if(item.itemId == R.id.reloadMenuItem){
            try {
                viewModel.requestImage()
                Toast.makeText(context,"Reloaded", Toast.LENGTH_SHORT).show()
            }catch (th: Throwable){
                Toast.makeText(context,"ERROR", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }


}
