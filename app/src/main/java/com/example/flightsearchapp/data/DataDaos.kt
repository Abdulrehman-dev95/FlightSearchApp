package com.example.flightsearchapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DataDao {
    @Query("SELECT * FROM airport WHERE name LIKE  :airportCode || '%' OR iata_code LIKE  :airportCode || '%' ORDER BY passengers DESC")
    fun listOfFlightsFromAirport(airportCode: String): Flow<List<Airports>>

    @Query("SELECT * FROM airport WHERE  iata_code NOT IN (:airportCode) ORDER BY passengers DESC")
    fun getAllFlightsExceptThis(airportCode: String): Flow<List<Airports>>

    @Query("SELECT name FROM airport WHERE iata_code = :airportCode")
    suspend fun getAirport(airportCode: String): String

    @Insert
    suspend fun insertFavourite(favourite: Favourite)

    @Query("SELECT * FROM favorite")
    suspend fun listOfFavourites(): List<Favourite>

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun deleteFavourite(departureCode: String, destinationCode: String)
    @Query("SELECT MAX(id) FROM favorite")
    suspend fun getMaxId(): Int?

}

