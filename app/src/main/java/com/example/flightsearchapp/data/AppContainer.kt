package com.example.flightsearchapp.data

import android.content.Context

interface AppContainer {
    val itemRepository: FlightSearchRepository
    val appPreferencesRepository: AppPreferencesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val itemRepository: FlightSearchRepository by lazy {
        OfflineSearchRepository(InventoryDatabase.getDb(context).dataDao())
    }
    override val appPreferencesRepository: AppPreferencesRepository by lazy {
        AppPreferencesRepository(context)
    }

}