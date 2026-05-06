package ru.ari.booking.presentation.contract

import ru.ari.booking.presentation.models.BookingRoomUiModel

sealed interface BookingScreenAction {
    data object Load : BookingScreenAction
    data object Refresh : BookingScreenAction
    data object RetryRooms : BookingScreenAction
    data object RetryBookings : BookingScreenAction
    data object OpenCorpusSelector : BookingScreenAction
    data object OpenRoomSelector : BookingScreenAction
    data object DismissRoomSelector : BookingScreenAction
    data class SelectCorpus(val corpus: String) : BookingScreenAction
    data class SelectRoom(val room: BookingRoomUiModel) : BookingScreenAction
    data class ChangeRoomSearchQuery(val value: String) : BookingScreenAction
    data class ChangeDate(val value: String) : BookingScreenAction
    data object ClickCreate : BookingScreenAction
    data class ClickBooking(val bookingId: Long) : BookingScreenAction
}
