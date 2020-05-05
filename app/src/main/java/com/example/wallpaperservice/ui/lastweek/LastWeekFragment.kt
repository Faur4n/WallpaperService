package com.example.wallpaperservice.ui.lastweek

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.example.wallpaperservice.R

class LastWeekFragment : Fragment() {

    companion object {
        fun newInstance() = LastWeekFragment()
    }

    private lateinit var viewModel: LastWeekViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.last_week_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LastWeekViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
