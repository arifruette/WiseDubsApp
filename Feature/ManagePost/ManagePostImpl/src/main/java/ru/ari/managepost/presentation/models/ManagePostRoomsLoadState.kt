package ru.ari.managepost.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ManagePostRoomsLoadState {

    @Immutable
    data object Loading : ManagePostRoomsLoadState

    @Immutable
    data object Content : ManagePostRoomsLoadState

    @Immutable
    data class Error(val message: String) : ManagePostRoomsLoadState
}
