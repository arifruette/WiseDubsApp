package ru.ari.booking.presentation.contract

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.ari.booking.presentation.models.BookingPostsLoadState
import ru.ari.booking.presentation.models.BookingRoomGroupUiModel
import ru.ari.booking.presentation.models.BookingRoomSheetStep
import ru.ari.booking.presentation.models.BookingRoomUiModel
import ru.ari.booking.presentation.models.BookingRoomsLoadState
import ru.ari.booking.presentation.models.BookingTimeMode
import ru.ari.booking.presentation.models.BookingUiModel

@Immutable
data class BookingScreenUiState(
    val roomsLoadState: BookingRoomsLoadState = BookingRoomsLoadState.Loading,
    val bookingsLoadState: BookingPostsLoadState = BookingPostsLoadState.Idle,
    val roomGroups: ImmutableList<BookingRoomGroupUiModel> = persistentListOf(),
    val bookings: ImmutableList<BookingUiModel> = persistentListOf(),
    val selectedCorpus: String = "",
    val selectedRoom: BookingRoomUiModel? = null,
    val roomSearchQuery: String = "",
    val selectedDate: String = "",
    val startTime: String = "",
    val endDate: String = "",
    val endTime: String = "",
    val durationMinutes: String = "60",
    val description: String = "",
    val timeMode: BookingTimeMode = BookingTimeMode.EndTime,
    val isSubmitting: Boolean = false,
    val activeRoomSheet: BookingRoomSheetStep = BookingRoomSheetStep.None
)
