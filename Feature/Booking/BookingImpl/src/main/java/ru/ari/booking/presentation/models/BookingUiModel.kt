package ru.ari.booking.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class BookingUiModel(
    val id: Long,
    val intervalText: String,
    val description: String,
    val roomName: String,
    val authorTelegramId: String,
    val isMine: Boolean
)
