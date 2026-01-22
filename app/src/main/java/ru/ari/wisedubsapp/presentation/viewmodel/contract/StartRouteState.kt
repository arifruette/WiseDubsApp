package ru.ari.wisedubsapp.presentation.viewmodel.contract

import ru.ari.navigation.Route

sealed interface StartRouteState {
    data class Computed(val route: Route): StartRouteState
    data object Loading: StartRouteState
}