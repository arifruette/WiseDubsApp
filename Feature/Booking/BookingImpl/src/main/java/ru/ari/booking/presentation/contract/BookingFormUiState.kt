package ru.ari.booking.presentation.contract

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.ari.booking.presentation.models.BookingRoomGroupUiModel
import ru.ari.booking.presentation.models.BookingRoomSheetStep
import ru.ari.booking.presentation.models.BookingRoomUiModel
import ru.ari.booking.presentation.models.BookingRoomsLoadState
import ru.ari.booking.presentation.models.BookingTimeMode
import ru.ari.booking.presentation.models.BookingUiModel

@Immutable
data class BookingFormUiState(
    val postId: Long? = null,
    val roomsLoadState: BookingRoomsLoadState = BookingRoomsLoadState.Loading,
    val isBookingLoading: Boolean = false,
    val loadError: String? = null,
    val roomGroups: ImmutableList<BookingRoomGroupUiModel> = persistentListOf(),
    val selectedCorpus: String = "",
    val selectedRoom: BookingRoomUiModel? = null,
    val roomSearchQuery: String = "",
    val selectedDate: String = "",
    val startTime: String = "",
    val endDate: String = "",
    val endTime: String = "",
    val durationMinutes: String = "60",
    val intervalError: String? = null,
    val description: String = "",
    val timeMode: BookingTimeMode = BookingTimeMode.EndTime,
    val isSubmitting: Boolean = false,
    val isDeleting: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val isIntersectionsLoading: Boolean = false,
    val intersections: ImmutableList<BookingUiModel> = persistentListOf(),
    val activeRoomSheet: BookingRoomSheetStep = BookingRoomSheetStep.None
) {
    val isEditMode: Boolean get() = postId != null
    val canSubmit: Boolean
        get() = !isSubmitting &&
            !isDeleting &&
            !isIntersectionsLoading &&
            intersections.isEmpty() &&
            intervalError == null
}
