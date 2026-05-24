package ru.ari.booking.presentation.contract

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.ari.booking.domain.models.MyBookingsPeriod
import ru.ari.booking.presentation.models.BookingPostsLoadState
import ru.ari.booking.presentation.models.BookingUiModel

@Immutable
data class MyBookingsUiState(
    val selectedPeriod: MyBookingsPeriod = MyBookingsPeriod.Upcoming,
    val loadState: BookingPostsLoadState = BookingPostsLoadState.Loading,
    val bookings: ImmutableList<BookingUiModel> = persistentListOf(),
    val isRefreshing: Boolean = false
)
