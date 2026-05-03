package ru.ari.booking.presentation.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class BookingRoomGroupUiModel(
    val corpus: String,
    val rooms: ImmutableList<BookingRoomUiModel>
)
