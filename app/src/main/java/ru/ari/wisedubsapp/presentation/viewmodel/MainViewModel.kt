package ru.ari.wisedubsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.cache.domain.datastore.DataStoreHelper
import ru.ari.navigation.Route
import ru.ari.wisedubsapp.presentation.viewmodel.contract.StartRouteState
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val dataStoreHelper: DataStoreHelper
): ViewModel() {

    private val _uiState = MutableStateFlow<StartRouteState>(StartRouteState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val token = dataStoreHelper.getSessionState().firstOrNull()?.token
            if (token == null) {
                _uiState.update { StartRouteState.Computed(Route.PreLogin) }
            } else {
                _uiState.update { StartRouteState.Computed(Route.PostLogin) }
            }
        }
    }
}