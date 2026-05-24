package ru.ari.booking.presentation.contract

import androidx.compose.runtime.Stable

@Stable
class BookingFormActionHandler(
    private val onAction: (BookingFormAction) -> Unit
) {
    fun onAction(action: BookingFormAction) = onAction.invoke(action)
}

