package ru.ari.booking.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
sealed interface BookingRoomSheetStep {
    data object None : BookingRoomSheetStep
    data object Corpus : BookingRoomSheetStep
    data object Room : BookingRoomSheetStep
}
