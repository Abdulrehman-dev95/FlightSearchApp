package com.example.flightsearchapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearchapp.FlightSearchApplication
import com.example.flightsearchapp.data.Airports
import com.example.flightsearchapp.data.Favourite
import com.example.flightsearchapp.data.FlightSearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val flightSearchRepository: FlightSearchRepository) :
    ViewModel() {
    var favouriteFlightsName by mutableStateOf(
        HomeScreenUi().favouriteFlights
    )
        private set
    var favouriteFlightsCode by mutableStateOf(
        HomeScreenUi().favouriteFlightsCode
    )
        private set
    var arriveList by mutableStateOf(
        HomeScreenUi().arriveList
    )
        private set

    var isFavourite by mutableStateOf(
        HomeScreenUi().isFavourite
    )
        private set
    var departList by mutableStateOf(HomeScreenUi().departList)


    private val query = MutableStateFlow(
        HomeScreenUi().query
    )
    val _query: MutableStateFlow<String> = query


    @OptIn(ExperimentalCoroutinesApi::class)
    val autoSuggestions: StateFlow<List<Airports>> = query.flatMapLatest {
        if (query.value.isNotEmpty()) {
            flightSearchRepository.getFlightsFromAirport(it)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )


    fun onSearch(query: String) {
        viewModelScope.launch {
            val nList = flightSearchRepository.getFlightsFromAirport(query).first()
            departList = nList
            val aList = flightSearchRepository.getAllFlightsExceptThis(query).first()
            arriveList = aList
        }

    }

    fun updateFavouriteState(isFavourite: Boolean) {
        this.isFavourite = isFavourite
    }

    fun updateQuery(query: String) {
        this.query.value = query
    }

    fun getFavourites() {
        viewModelScope.launch {
            val favourites = flightSearchRepository.getFavourites()
            favouriteFlightsCode = favourites
            favourites.forEach {
                val dName = flightSearchRepository.getAirport(it.departureCode)
                val aName = flightSearchRepository.getAirport(it.destinationCode)
                favouriteFlightsName = favouriteFlightsName.plus(Pair(dName, aName))
            }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                val flightSearchRepository = application.container.itemRepository
                HomeScreenViewModel(flightSearchRepository = flightSearchRepository)
            }
        }
    }
}

data class HomeScreenUi(
    val query: String = "",
    val isFavourite: Boolean = false,
    val departList: List<Airports> = emptyList(),
    val favouriteFlights: List<Pair<String, String>> = emptyList(),
    val arriveList: List<Airports> = emptyList(),
    val favouriteFlightsCode: List<Favourite> = emptyList()
)

