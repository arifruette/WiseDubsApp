package ru.ari.booking.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
sealed interface BookingRoomsLoadState {
    data object Loading : BookingRoomsLoadState
    data object Content : BookingRoomsLoadState
    data object Empty : BookingRoomsLoadState
    data class Error(val message: String) : BookingRoomsLoadState
}
