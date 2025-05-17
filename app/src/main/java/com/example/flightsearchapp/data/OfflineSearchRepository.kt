package com.example.flightsearchapp.data

import kotlinx.coroutines.flow.Flow

class OfflineSearchRepository(
    private val dataDao: DataDao
) : FlightSearchRepository {
    override suspend fun getFavourites(): List<Favourite> {
        return dataDao.listOfFavourites()
    }

    override suspend fun getAirport(airportCode: String): String {
        return dataDao.getAirport(airportCode)
    }

    override fun getAllFlightsExceptThis(airportCode: String): Flow<List<Airports>> {
        return dataDao.getAllFlightsExceptThis(airportCode)
    }

    override suspend fun insertFavourite(favourite: Favourite) {
        dataDao.insertFavourite(favourite)
    }

    override fun getFlightsFromAirport(airportCode: String): Flow<List<Airports>> {
        return dataDao.listOfFlightsFromAirport(airportCode)
    }
}