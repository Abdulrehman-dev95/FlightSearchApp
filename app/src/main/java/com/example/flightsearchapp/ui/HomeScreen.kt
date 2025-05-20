package com.example.flightsearchapp.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearchapp.R
import com.example.flightsearchapp.data.Favourite
import com.example.flightsearchapp.utils.Utils

/**
 * Composable function that displays the home screen of the application.
 *
 * This function sets up the basic layout of the screen using a Scaffold,
 * which includes a top app bar and the main content area.
 *
 * @param modifier Modifier for this composable. Defaults to Modifier.
 */
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
        val query = viewModel.userQuery.collectAsState()
        val dList = viewModel.departList
        val arriveList = viewModel.arriveList
        val favListName = viewModel.favouriteFlightsName
        val favListCode = viewModel.favouriteFlightsCode
        val isFavourite = viewModel.isFavouriteList
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
                        contentDescription = stringResource(R.string.search)
                    )
                }, placeholder = {
                    Text(
                        text = stringResource(R.string.search_departure_airport)
                    )
                },
                trailingIcon = {
                    MicButton {
                        viewModel.updateQuery(it)
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


                if (query.value.isNotBlank() && dList.isNotEmpty()) {


                    AnimatedVisibility(visible = !isVisible) {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            item {
                                Text(
                                    text = stringResource(R.string.flights_from, dList[0].name),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                )
                            }


                            items(arriveList.size) {
                                SearchedItem(
                                    departureName = dList[0].name,
                                    departureCode = dList[0].iataCode,
                                    arrivalCode = arriveList[it].iataCode,
                                    arrivalName = arriveList[it].name,
                                    isFavourite = isFavourite.contains("${dList[0].iataCode}-${arriveList[it].iataCode}"),
                                    onStarClick = {
                                        viewModel.toggleFavourites(
                                            iataACode = arriveList[it].iataCode,
                                            iataDCode = dList[0].iataCode
                                        )

                                    }
                                )
                            }
                        }
                    }
                } else {
                    when {
                        viewModel.isLoadingFavourites -> {
                            CircularProgressIndicator()
                        }

                        favListCode.isNotEmpty() -> {
                            FavouriteScreen(
                                favouritesName = favListName,
                                favouritesCode = favListCode,
                                isFavourite = isFavourite,
                                onStarClick = { dCode, aCode ->
                                    viewModel.toggleFavourites(dCode, aCode)
                                })
                        }

                        else -> {
                            Text(stringResource(R.string.no_favourite_flights))
                        }
                    }
                }
            }

        }


    }
}

/**
 * This composable function displays a single auto-suggestion item in a list.
 */
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

/**
 * This composable function displays a single searched item in a list.
 */
@Composable
fun SearchedItem(
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
                    text = stringResource(R.string.depart)
                )
                Text(
                    text = departureString
                )
                Text(
                    text = stringResource(R.string.arrive)
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
                    contentDescription = stringResource(R.string.favourite),
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

/**
 * This composable func display the top app bar of the application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.flight_search_app),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xff00aeff)
        )
    )

}

/**
 * This composable function displays a list of favourite flights.
 */

@Composable
fun FavouriteScreen(
    modifier: Modifier = Modifier,
    favouritesName: List<Pair<String, String>>,
    favouritesCode: List<Favourite>,
    isFavourite: List<String>,
    onStarClick: (String, String) -> Unit
) {
    Text(
        text = stringResource(R.string.favourite_flights),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )


    LazyColumn(modifier = modifier) {
        items(favouritesCode.size) {
            SearchedItem(
                departureCode = favouritesCode[it].departureCode,
                departureName = favouritesName[it].first,
                arrivalCode = favouritesCode[it].destinationCode,
                arrivalName = favouritesName[it].second,
                isFavourite = isFavourite.contains("${favouritesCode[it].departureCode}-${favouritesCode[it].destinationCode}"),
                onStarClick = {
                    onStarClick(
                        favouritesCode[it].departureCode,
                        favouritesCode[it].destinationCode
                    )
                }

            )

        }
    }

}

/**
 * This composable function displays a microphone button that triggers speech recognition.
 */

@Composable
fun MicButton(
    onResult: (String) -> Unit
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }

    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                isListening = true
                speechRecognizer.startListening(recognizerIntent)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.speech_recognition_not_available_on_this_device),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.microphone_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    DisposableEffect(Unit) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                isListening = false
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> context.getString(R.string.audio_recording_error)
                    SpeechRecognizer.ERROR_CLIENT -> context.getString(R.string.client_side_error)
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> context.getString(R.string.insufficient_permissions)
                    SpeechRecognizer.ERROR_NETWORK -> context.getString(R.string.network_error)
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> context.getString(R.string.network_timeout)
                    SpeechRecognizer.ERROR_NO_MATCH -> context.getString(R.string.no_match_found)
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> context.getString(R.string.recognizer_busy)
                    SpeechRecognizer.ERROR_SERVER -> context.getString(R.string.server_error)
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> context.getString(R.string.no_speech_input)
                    else -> context.getString(R.string.unknown_error)
                }
                Toast.makeText(context,
                    context.getString(R.string.recognition_failed, errorMsg), Toast.LENGTH_LONG).show()
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { result ->
                    onResult(result)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer.setRecognitionListener(listener)

        onDispose {
            speechRecognizer.destroy()
        }
    }

    val micColor by animateColorAsState(
        if (isListening) Color.Red else Color.Gray,
        animationSpec = tween(durationMillis = 500),
        label = stringResource(R.string.miccoloranimation)
    )

    IconButton(
        onClick = {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_mic),
            contentDescription = stringResource(R.string.mic),
            tint = micColor,
            modifier = Modifier.size(if (isListening) 36.dp else 30.dp)
        )
    }
}




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}