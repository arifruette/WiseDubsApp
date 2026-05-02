package ru.ari.wisedubsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.cache.domain.datastore.DataStoreHelper
import ru.ari.cache.domain.models.SessionState
import ru.ari.navigation.Route
import ru.ari.wisedubsapp.presentation.viewmodel.contract.StartRouteState
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val dataStoreHelper: DataStoreHelper
): ViewModel() {

    private val _uiState = MutableStateFlow<StartRouteState>(StartRouteState.Loading)
    val uiState = _uiState.asStateFlow()

    private var postLoginGeneration = 0
    private var wasPostLogin = false

    init {
        viewModelScope.launch {
            dataStoreHelper.getSessionState()
                .map { sessionState ->
                    if (sessionState?.isComplete != true) {
                        wasPostLogin = false
                        StartRouteState.Computed(
                            route = Route.PreLogin,
                            navigationResetKey = PRE_LOGIN_NAVIGATION_KEY
                        )
                    } else {
                        if (!wasPostLogin) {
                            postLoginGeneration += 1
                            wasPostLogin = true
                        }
                        StartRouteState.Computed(
                            route = Route.PostLogin,
                            navigationResetKey = sessionState.toNavigationResetKey(postLoginGeneration)
                        )
                    }
                }
                .distinctUntilChanged()
                .collect { routeState ->
                    _uiState.update { routeState }
                }
        }
    }

    private companion object {
        const val PRE_LOGIN_NAVIGATION_KEY = "pre_login"
    }
}

private val SessionState.isComplete: Boolean
    get() = token.isNotBlank() && userId != null && userTelegramId != null

private fun SessionState.toNavigationResetKey(generation: Int): String =
    "post_login_${checkNotNull(userId)}_${token.hashCode()}_$generation"
