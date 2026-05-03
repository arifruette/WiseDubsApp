package ru.ari.booking.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
sealed interface BookingPostsLoadState {
    data object Idle : BookingPostsLoadState
    data object Loading : BookingPostsLoadState
    data object Content : BookingPostsLoadState
    data object Empty : BookingPostsLoadState
    data class Error(val message: String) : BookingPostsLoadState
}
