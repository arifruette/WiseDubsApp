package ru.ari.wisedubsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.cache.datastore.DataStoreHelper
import ru.ari.navigation.Route
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val dataStoreHelper: DataStoreHelper
): ViewModel() {

    private val _uiState = MutableStateFlow<StartRouteState>(StartRouteState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val token = dataStoreHelper.getToken().firstOrNull()
            if (token == null) {
                _uiState.update { StartRouteState.Computed(Route.PreLogin) }
            } else {
                _uiState.update { StartRouteState.Computed(Route.PostLogin) }
            }
        }
    }
}