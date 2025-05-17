package com.example.flightsearchapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearchapp.R
import com.example.flightsearchapp.data.Favourite
import com.example.flightsearchapp.utils.Utils

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar()
        }
    ) {
        SearchLayout(modifier = Modifier.padding(it))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLayout(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel(factory = HomeScreenViewModel.factory)
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
    ) {
        val autoSuggestions = viewModel.autoSuggestions.collectAsState()
        val query = viewModel._query.collectAsState()
        val dList = viewModel.departList
        val arriveList = viewModel.arriveList
        val favListName = viewModel.favouriteFlightsName
        val favListCode = viewModel.favouriteFlightsCode
        var isVisible by remember {
            mutableStateOf(false)
        }
        Card(
            shape = RoundedCornerShape(50.dp)
        ) {
            TextField(
                value = query.value,
                onValueChange = {
                    isVisible = true
                    viewModel.updateQuery(it)
                }, leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }, placeholder = {
                    Text(
                        text = "Search Departure Airport"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mic),
                            contentDescription = "mic"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = true,
                    imeAction = ImeAction.Search

                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.onSearch(query.value)
                        isVisible = false
                    }
                ),
                colors = TextFieldDefaults.colors(

                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


        }
        Spacer(
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(visible = isVisible) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        items(autoSuggestions.value.size) {
                            AutoSuggestionsItem(
                                airportCode = autoSuggestions.value[it].iataCode,
                                airportName = autoSuggestions.value[it].name,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.onSearch(autoSuggestions.value[it].iataCode)
                                        isVisible = false
                                    }

                            )
                        }
                    }
                }


                if (dList.isNotEmpty()) {
                    Text(
                        text = "Flight From ${dList[0].iataCode}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    LazyColumn {
                        items(arriveList.size) {
                            SearchItem(
                                departureName = dList[0].name,
                                departureCode = dList[0].iataCode,
                                arrivalCode = arriveList[it].iataCode,
                                arrivalName = arriveList[it].name,
                                isFavourite = viewModel.isFavourite,
                                onStarClick = {
                                    viewModel.updateFavouriteState(isFavourite = !viewModel.isFavourite)

                                }
                            )
                        }
                    }
                } else  {
                    Text(
                        text = "No favorite flights",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

        }


    }
}

@Composable
fun AutoSuggestionsItem(modifier: Modifier = Modifier, airportCode: String, airportName: String) {
    val annotatedString = remember {
        Utils.stringBuilder(airportCode, airportName)
    }

    Text(
        text = annotatedString,
        modifier = modifier
    )
}

@Composable
fun SearchItem(
    modifier: Modifier = Modifier,
    departureCode: String,
    departureName: String,
    arrivalCode: String,
    arrivalName: String,
    isFavourite: Boolean,
    onStarClick: () -> Unit = {}
) {
    val departureString = remember {
        Utils.stringBuilder(departureCode, departureName)
    }
    val arrivalString = remember {
        Utils.stringBuilder(arrivalCode, arrivalName)
    }

    Card(
        modifier = modifier.padding(bottom = 16.dp),
        shape = RoundedCornerShape(topEnd = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.weight(1f)) {
                Text(
                    text = "DEPART"
                )
                Text(
                    text = departureString
                )
                Text(
                    text = "ARRIVE"
                )
                Text(
                    text = arrivalString
                )

            }
            IconButton(
                onClick = onStarClick,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {

                Icon(
                    imageVector = if (isFavourite) {
                        Icons.Filled.Star
                    } else {
                        Icons.Outlined.Star
                    },
                    contentDescription = "Favourite",
                    tint = if (isFavourite) {
                        Color.Yellow
                    } else {
                        Color.Gray
                    },
                    modifier = Modifier.size(32.dp)
                )
            }
        }


    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Flight Search App",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xff00aeff)
        )
    )

}


@Composable
fun FavouriteScreen(
    modifier: Modifier = Modifier,
    favouritesName: List<Pair<String, String>>,
    favouritesCode: List<Favourite>,
    isFavourite: Boolean
) {
    LazyColumn(modifier = modifier) {
        items(favouritesCode.size) {
            SearchItem(
                departureCode = favouritesCode[it].departureCode,
                departureName = favouritesName[it].first,
                arrivalCode = favouritesCode[it].destinationCode,
                arrivalName = favouritesName[it].second,
                isFavourite = isFavourite

            )

        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}