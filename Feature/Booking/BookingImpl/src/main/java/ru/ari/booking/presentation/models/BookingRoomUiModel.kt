package ru.ari.booking.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class BookingRoomUiModel(
    val id: Int,
    val name: String,
    val corpus: String
)
