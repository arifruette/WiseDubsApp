package ru.ari.booking.presentation.contract

fun interface BookingActionHandler {
    fun onAction(action: BookingScreenAction)
}
