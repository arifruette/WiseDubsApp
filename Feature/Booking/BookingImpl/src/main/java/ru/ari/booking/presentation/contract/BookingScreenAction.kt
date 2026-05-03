package ru.ari.booking.presentation.contract

import ru.ari.booking.presentation.models.BookingRoomUiModel
import ru.ari.booking.presentation.models.BookingTimeMode

sealed interface BookingScreenAction {
    data object Load : BookingScreenAction
    data object RetryRooms : BookingScreenAction
    data object RetryBookings : BookingScreenAction
    data object OpenCorpusSelector : BookingScreenAction
    data object OpenRoomSelector : BookingScreenAction
    data object DismissRoomSelector : BookingScreenAction
    data class SelectCorpus(val corpus: String) : BookingScreenAction
    data class SelectRoom(val room: BookingRoomUiModel) : BookingScreenAction
    data class ChangeRoomSearchQuery(val value: String) : BookingScreenAction
    data class ChangeDate(val value: String) : BookingScreenAction
    data class ChangeStartTime(val value: String) : BookingScreenAction
    data class ChangeEndDate(val value: String) : BookingScreenAction
    data class ChangeEndTime(val value: String) : BookingScreenAction
    data class ChangeDuration(val value: String) : BookingScreenAction
    data class ChangeTimeMode(val mode: BookingTimeMode) : BookingScreenAction
    data class ChangeDescription(val value: String) : BookingScreenAction
    data object Submit : BookingScreenAction
}
