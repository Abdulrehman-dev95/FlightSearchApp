package com.example.flightsearchapp.data

import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {
   suspend fun getFavourites(): List<Favourite>
    fun getFlightsFromAirport(airportCode: String): Flow<List<Airports>>
    suspend fun getAirport(airportCode: String): String
    suspend fun insertFavourite(favourite: Favourite)
    fun getAllFlightsExceptThis(airportCode: String): Flow<List<Airports>>
}
