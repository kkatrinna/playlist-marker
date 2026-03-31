package com.example.playlist_maker_android_yulyakondratiuk.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_android_yulyakondratiuk.creator.Creator
import com.example.playlist_maker_android_yulyakondratiuk.domain.TracksRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()

    private var searchJob: Job? = null

    fun search(whatSearch: String) {
        if (whatSearch.isBlank()) {
            clearSearch()
            return
        }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(500)

            if (!Creator.isNetworkAvailable()) {
                _searchScreenState.update { SearchState.ConnectionError }
                return@launch
            }

            try {
                _searchScreenState.update { SearchState.Searching }
                val list = tracksRepository.searchTracks(whatSearch)

                if (list.isEmpty()) {
                    _searchScreenState.update { SearchState.Success(emptyList()) }
                } else {
                    _searchScreenState.update { SearchState.Success(list) }
                }
            } catch (e: IOException) {
                _searchScreenState.update { SearchState.ConnectionError }
            } catch (e: Exception) {
                val errorMessage = com.example.playlist_maker_android_yulyakondratiuk.R.string.search_error
                val unknownState = com.example.playlist_maker_android_yulyakondratiuk.R.string.unknown_state
                _searchScreenState.update {
                    SearchState.Fail("$errorMessage: ${e.message ?: unknownState}")
                }
            }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchScreenState.update { SearchState.Initial }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = Creator.getTracksRepository()
                    return SearchViewModel(repository) as T
                }
            }
    }
}