package ru.ari.booking.presentation.contract

sealed interface BookingScreenUiEffect {
    data class ShowMessage(val message: String) : BookingScreenUiEffect
    data class OpenBookingForm(
        val roomId: Int?,
        val date: String?
    ) : BookingScreenUiEffect
    data class OpenEditBooking(val bookingId: Long) : BookingScreenUiEffect
}
