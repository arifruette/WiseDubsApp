package ru.ari.booking.presentation.contract

import ru.ari.booking.presentation.models.BookingRoomUiModel
import ru.ari.booking.presentation.models.BookingTimeMode

sealed interface BookingFormAction {
    data class Load(
        val postId: Long?,
        val initialRoomId: Int?,
        val initialDate: String?
    ) : BookingFormAction
    data object Retry : BookingFormAction
    data object OpenCorpusSelector : BookingFormAction
    data object OpenRoomSelector : BookingFormAction
    data object DismissRoomSelector : BookingFormAction
    data class SelectCorpus(val corpus: String) : BookingFormAction
    data class SelectRoom(val room: BookingRoomUiModel) : BookingFormAction
    data class ChangeRoomSearchQuery(val value: String) : BookingFormAction
    data class ChangeDate(val value: String) : BookingFormAction
    data class ChangeStartTime(val value: String) : BookingFormAction
    data class ChangeEndDate(val value: String) : BookingFormAction
    data class ChangeEndTime(val value: String) : BookingFormAction
    data class ChangeDuration(val value: String) : BookingFormAction
    data class ChangeTimeMode(val mode: BookingTimeMode) : BookingFormAction
    data class ChangeDescription(val value: String) : BookingFormAction
    data object ClickDelete : BookingFormAction
    data object DismissDelete : BookingFormAction
    data object ConfirmDelete : BookingFormAction
    data object Submit : BookingFormAction
    data object ClickBack : BookingFormAction
}
