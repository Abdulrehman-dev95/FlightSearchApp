package com.example.flightsearchapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferencesRepository(private val context: Context) {
    private val Context.dataStore by preferencesDataStore("app_preferences")

    companion object {
        val LAST_QUERY = stringPreferencesKey("last_query")
    }

    val lastQuery: Flow<String> = context.dataStore.data.map {
        preferences ->
        preferences[LAST_QUERY] ?: ""
    }


    suspend fun saveLastQuery(query: String) {
context.dataStore.edit {
    preferences ->
    preferences[LAST_QUERY] = query

}


    }

}