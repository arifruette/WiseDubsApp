package ru.ari.booking.presentation.contract

sealed interface MyBookingsUiEffect {
    data object NavigateBack : MyBookingsUiEffect
    data class OpenEditBooking(val bookingId: Long) : MyBookingsUiEffect
    data class ShowMessage(val message: String) : MyBookingsUiEffect
}

