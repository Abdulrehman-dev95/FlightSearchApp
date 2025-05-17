package com.example.flightsearchapp.data

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(tableName = "airport")
data class Airports(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    @ColumnInfo(name = "iata_code") val iataCode: String,
    val passengers: Int,
)
