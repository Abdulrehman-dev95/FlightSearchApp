package com.example.flightsearchapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [Airports::class, Favourite::class], version = 4, exportSchema = false)
abstract class InventoryDatabase: RoomDatabase() {
   abstract fun dataDao(): DataDao
companion object {
    @Volatile
   private var INSTANCE: InventoryDatabase? = null
    fun getDb(context: Context): InventoryDatabase {
        return INSTANCE?: synchronized(this) {
            Room.databaseBuilder(context, InventoryDatabase::class.java, "flight_database")
                .createFromAsset("database/flight_search.db")
                .build()
                .also { INSTANCE = it }

        }
    }

}

}
