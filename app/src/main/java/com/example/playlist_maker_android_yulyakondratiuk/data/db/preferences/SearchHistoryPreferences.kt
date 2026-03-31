package com.example.playlist_maker_android_yulyakondratiuk.data.db.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

class SearchHistoryPreferences(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName("search-history"))
) {
    private val preferencesKey = stringPreferencesKey("search_history")
    private val maxEntries = 10
    private val separator = ","

    fun addEntry(word: String) {
        if (word.isEmpty()) return

        coroutineScope.launch {
            context.dataStore.edit { preferences ->
                val historyString = preferences[preferencesKey].orEmpty()
                val history = if (historyString.isNotEmpty()) {
                    historyString.split(separator).toMutableList()
                } else {
                    mutableListOf()
                }

                history.remove(word)
                history.add(0, word)

                val subList = if (history.size > maxEntries) {
                    history.subList(0, maxEntries)
                } else {
                    history
                }

                val updatedString = subList.joinToString(separator)
                preferences[preferencesKey] = updatedString
            }
        }
    }

    suspend fun getEntries(): List<String> {
        return context.dataStore.data.map { preferences ->
            val historyString = preferences[preferencesKey].orEmpty()
            if (historyString.isNotEmpty()) {
                historyString.split(separator)
            } else {
                emptyList()
            }
        }.first()
    }

    fun getEntriesFlow(): Flow<List<String>> {
        return context.dataStore.data.map { preferences ->
            val historyString = preferences[preferencesKey].orEmpty()
            if (historyString.isNotEmpty()) {
                historyString.split(separator)
            } else {
                emptyList()
            }
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(preferencesKey)
        }
    }
}