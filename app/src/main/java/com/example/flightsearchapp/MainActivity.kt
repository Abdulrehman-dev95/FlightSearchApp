package com.example.flightsearchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.flightsearchapp.ui.HomeScreen
import com.example.flightsearchapp.ui.theme.FlightSearchAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlightSearchAppTheme {
                FlightSearchApp()
            }
        }
    }
}

@Composable
fun FlightSearchApp() {
    HomeScreen()
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppPreview() {
    FlightSearchApp()
}