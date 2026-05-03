package ru.ari.booking.presentation.contract

sealed interface BookingScreenUiEffect {
    data class ShowMessage(val message: String) : BookingScreenUiEffect
    data object ScrollToBookings : BookingScreenUiEffect
}
