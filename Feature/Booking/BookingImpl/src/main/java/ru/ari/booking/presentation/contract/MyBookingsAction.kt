package ru.ari.booking.presentation.contract

import ru.ari.booking.domain.models.MyBookingsPeriod

sealed interface MyBookingsAction {
    data object Load : MyBookingsAction
    data object Refresh : MyBookingsAction
    data class SelectPeriod(val period: MyBookingsPeriod) : MyBookingsAction
    data class ClickEdit(val bookingId: Long) : MyBookingsAction
    data object ClickBack : MyBookingsAction
}
