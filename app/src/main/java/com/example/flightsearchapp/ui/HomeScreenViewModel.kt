package com.example.flightsearchapp.ui


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearchapp.FlightSearchApplication
import com.example.flightsearchapp.data.Airports
import com.example.flightsearchapp.data.AppPreferencesRepository
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
/**
 * ViewModel for the home screen, managing flight search and favourite flights.
 *
 * This ViewModel interacts with [FlightSearchRepository] to fetch flight data
 * and [AppPreferencesRepository] to manage user preferences like the last search query.
 * It exposes UI state through Compose `mutableStateOf` properties and uses Kotlin Flows
 * for reactive data streams like auto-suggestions.
 *
 * @param flightSearchRepository Repository for accessing flight and airport data.
 * @param appPreferencesRepository Repository for managing application preferences.
 */
class HomeScreenViewModel(
    private val flightSearchRepository: FlightSearchRepository,
    private val appPreferencesRepository: AppPreferencesRepository
) :
    ViewModel() {

    var favouriteFlightsName by mutableStateOf(emptyList<Pair<String, String>>())
        private set

    var favouriteFlightsCode by mutableStateOf(emptyList<Favourite>())
        private set

    var isFavouriteList by mutableStateOf(listOf<String>())
        private set

    var isLoadingFavourites by mutableStateOf(true)
        private set

    var arriveList by mutableStateOf(emptyList<Airports>())
        private set

    var departList by mutableStateOf(emptyList<Airports>())
        private set

    private val query = MutableStateFlow("")
    val userQuery: MutableStateFlow<String> = query

    @OptIn(ExperimentalCoroutinesApi::class)
    val autoSuggestions: StateFlow<List<Airports>> = query.flatMapLatest {
        if (it.isNotEmpty()) {
            flightSearchRepository.getFlightsFromAirport(it)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    /**
     * Searches for flights based on the provided query.
     */
    fun onSearch(query: String) {
        viewModelScope.launch {
            departList = flightSearchRepository.getFlightsFromAirport(query).first()
            arriveList = flightSearchRepository.getAllFlightsExceptThis(query).first()
        }
    }

    /**
     * Updates the user's query and saves it to preferences.
     */
    fun updateQuery(query: String) {
        this.query.value = query
        viewModelScope.launch {
            appPreferencesRepository.saveLastQuery(query)
        }
    }

    /**
     * Toggles the favourite status of a flight by adding or removing in fav table.
     */
    fun toggleFavourites(iataDCode: String, iataACode: String) {
        val iataCode = "$iataDCode-$iataACode"
        viewModelScope.launch {
            if (isFavouriteList.contains(iataCode)) {
                flightSearchRepository.deleteFavourite(iataDCode, iataACode)
            } else {
                val newId = flightSearchRepository.getMaxFavouriteId() + 1
                flightSearchRepository.insertFavourite(
                    Favourite(
                        id = newId,
                        departureCode = iataDCode,
                        destinationCode = iataACode
                    )
                )
            }
            loadFavourites()

        }
    }

    /**
     * Loads favourite flights from the repository.
     */
    private fun loadFavourites() {
        viewModelScope.launch {
            isLoadingFavourites = true
            val favourites = flightSearchRepository.getFavourites()
            favouriteFlightsCode = favourites
            favouriteFlightsName = favourites.map {
                val dName = flightSearchRepository.getAirport(it.departureCode)
                val aName = flightSearchRepository.getAirport(it.destinationCode)
                Pair(dName, aName)
            }
            isFavouriteList = favourites.map { "${it.departureCode}-${it.destinationCode}" }
            isLoadingFavourites = false
        }
    }

    /**
     * Loads the last search query from preferences.
     */

    private fun loadLastQuery() {
        viewModelScope.launch {
            val lastQuery = appPreferencesRepository.lastQuery.first()
            updateQuery(lastQuery)
        }

    }

    /**
     * Initializes the ViewModel by loading favourites and last query.
     */
    init {
        loadFavourites()
        loadLastQuery()
    }

    /**
     * Factory for creating [HomeScreenViewModel].
     */
    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlightSearchApplication)
                val flightSearchRepository = application.container.itemRepository
                val appPreferencesRepository = application.container.appPreferencesRepository
                HomeScreenViewModel(flightSearchRepository, appPreferencesRepository)
            }
        }
    }
}

