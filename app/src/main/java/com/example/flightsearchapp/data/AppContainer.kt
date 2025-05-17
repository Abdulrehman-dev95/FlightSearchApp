package com.example.flightsearchapp.data

import android.content.Context

interface AppContainer {
    val itemRepository: FlightSearchRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val itemRepository: FlightSearchRepository by lazy  {
        OfflineSearchRepository(InventoryDatabase.getDb(context).dataDao())
    }

}