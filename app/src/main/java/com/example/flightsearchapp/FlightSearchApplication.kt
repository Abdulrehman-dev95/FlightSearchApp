package com.example.flightsearchapp

import android.app.Application
import com.example.flightsearchapp.data.AppContainer
import com.example.flightsearchapp.data.AppDataContainer

class FlightSearchApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
