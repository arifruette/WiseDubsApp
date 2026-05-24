@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.booking.domain.interactor.BookingInteractor
import ru.ari.booking.domain.models.Booking
import ru.ari.booking.domain.models.GroupedRooms
import ru.ari.booking.presentation.contract.BookingScreenAction
import ru.ari.booking.presentation.contract.BookingScreenUiEffect
import ru.ari.booking.presentation.contract.BookingScreenUiState
import ru.ari.booking.presentation.models.BookingPostsLoadState
import ru.ari.booking.presentation.models.BookingRoomGroupUiModel
import ru.ari.booking.presentation.models.BookingRoomSheetStep
import ru.ari.booking.presentation.models.BookingRoomUiModel
import ru.ari.booking.presentation.models.BookingRoomsLoadState
import ru.ari.booking.presentation.models.BookingUiModel
import ru.ari.network.domain.models.Result
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

class BookingViewModel @Inject constructor(
    private val bookingInteractor: BookingInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BookingScreenUiState(selectedDate = LocalDate.now().format(DATE_FORMATTER))
    )
    val uiState: StateFlow<BookingScreenUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<BookingScreenUiEffect>()
    val uiEffect: SharedFlow<BookingScreenUiEffect> = _uiEffect.asSharedFlow()

    private var roomsLoaded = false
    private var observeBookingsJob: Job? = null
    private var refreshBookingsJob: Job? = null
    private var observedScope: Pair<Int, String>? = null

    fun onAction(action: BookingScreenAction) {
        when (action) {
            BookingScreenAction.Load -> {
                if (!roomsLoaded) loadRooms()
                observeAndRefreshBookings(userInitiated = false)
            }
            BookingScreenAction.Refresh -> refreshCurrentScreen()
            BookingScreenAction.RetryRooms -> loadRooms()
            BookingScreenAction.RetryBookings -> observeAndRefreshBookings(userInitiated = true)
            BookingScreenAction.OpenCorpusSelector -> {
                if (_uiState.value.roomsLoadState == BookingRoomsLoadState.Content) {
                    _uiState.update { it.copy(activeRoomSheet = BookingRoomSheetStep.Corpus) }
                }
            }
            BookingScreenAction.OpenRoomSelector -> {
                val state = _uiState.value
                if (state.roomsLoadState == BookingRoomsLoadState.Content && state.selectedCorpus.isNotBlank()) {
                    _uiState.update { it.copy(activeRoomSheet = BookingRoomSheetStep.Room) }
                }
            }
            BookingScreenAction.DismissRoomSelector -> {
                _uiState.update { it.copy(activeRoomSheet = BookingRoomSheetStep.None) }
            }
            is BookingScreenAction.SelectCorpus -> {
                _uiState.update { state ->
                    state.copy(
                        selectedCorpus = action.corpus,
                        selectedRoom = null,
                        roomSearchQuery = "",
                        activeRoomSheet = BookingRoomSheetStep.None,
                        bookingsLoadState = BookingPostsLoadState.Idle,
                        bookings = emptyList<BookingUiModel>().toImmutableList()
                    )
                }
                observeBookingsJob?.cancel()
                observedScope = null
            }
            is BookingScreenAction.SelectRoom -> {
                _uiState.update {
                    it.copy(selectedRoom = action.room, activeRoomSheet = BookingRoomSheetStep.None)
                }
                observeAndRefreshBookings(userInitiated = false)
            }
            is BookingScreenAction.ChangeRoomSearchQuery -> {
                _uiState.update { it.copy(roomSearchQuery = action.value.take(40)) }
            }
            is BookingScreenAction.ChangeDate -> {
                _uiState.update { it.copy(selectedDate = action.value.take(DATE_INPUT_LENGTH)) }
                observeAndRefreshBookings(userInitiated = false)
            }
            BookingScreenAction.ClickCreate -> {
                val state = _uiState.value
                emitEffect(
                    BookingScreenUiEffect.OpenBookingForm(
                        roomId = state.selectedRoom?.id,
                        date = state.selectedDate.takeIf { it.isNotBlank() }
                    )
                )
            }
            is BookingScreenAction.ClickBooking -> {
                val booking = _uiState.value.bookings.firstOrNull { it.id == action.bookingId }
                if (booking?.isMine == true) {
                    emitEffect(BookingScreenUiEffect.OpenEditBooking(action.bookingId))
                }
            }
        }
    }

    private fun refreshCurrentScreen() {
        if (_uiState.value.selectedRoom == null) {
            loadRooms(userInitiated = true)
        } else {
            observeAndRefreshBookings(userInitiated = true)
        }
    }

    private fun loadRooms(userInitiated: Boolean = false) {
        _uiState.update {
            it.copy(
                roomsLoadState = if (userInitiated && roomsLoaded) it.roomsLoadState else BookingRoomsLoadState.Loading,
                isRoomsRefreshing = userInitiated,
                activeRoomSheet = BookingRoomSheetStep.None
            )
        }
        viewModelScope.launch {
            when (val result = bookingInteractor.getGroupedRooms()) {
                is Result.Success -> {
                    roomsLoaded = true
                    val groups = result.data
                        .map { it.toUiModel() }
                        .filter { it.rooms.isNotEmpty() }
                        .toImmutableList()
                    _uiState.update {
                        it.copy(
                            roomGroups = groups,
                            roomsLoadState = if (groups.isEmpty()) BookingRoomsLoadState.Empty else BookingRoomsLoadState.Content,
                            isRoomsRefreshing = false
                        )
                    }
                }
                is Result.Error -> _uiState.update {
                    it.copy(roomsLoadState = BookingRoomsLoadState.Error(result.message), isRoomsRefreshing = false)
                }
                is Result.Exception -> _uiState.update {
                    it.copy(
                        roomsLoadState = BookingRoomsLoadState.Error(result.error.message ?: "Не удалось загрузить комнаты"),
                        isRoomsRefreshing = false
                    )
                }
            }
        }
    }

    private fun observeAndRefreshBookings(userInitiated: Boolean) {
        val state = _uiState.value
        val room = state.selectedRoom ?: return
        val date = state.selectedDate.parseDateOrNull() ?: return
        val dateKey = date.format(DATE_FORMATTER)
        val scope = room.id to dateKey

        if (observedScope != scope) {
            observedScope = scope
            observeBookingsJob?.cancel()
            observeBookingsJob = viewModelScope.launch {
                bookingInteractor.observeBookings(room.id, dateKey).collect { bookings ->
                    val uiBookings = bookings.sortedBy { it.timeStart }.map { it.toUiModel() }.toImmutableList()
                    _uiState.update { current ->
                        current.copy(
                            bookings = uiBookings,
                            bookingsLoadState = when {
                                uiBookings.isNotEmpty() -> BookingPostsLoadState.Content
                                current.bookingsLoadState == BookingPostsLoadState.Loading -> BookingPostsLoadState.Loading
                                else -> BookingPostsLoadState.Empty
                            },
                            isBookingsRefreshing = false
                        )
                    }
                }
            }
        }

        refreshBookingsJob?.cancel()
        val hasCache = _uiState.value.bookings.isNotEmpty()
        _uiState.update {
            it.copy(
                bookingsLoadState = if (!hasCache) BookingPostsLoadState.Loading else it.bookingsLoadState,
                isBookingsRefreshing = userInitiated && hasCache
            )
        }
        refreshBookingsJob = viewModelScope.launch {
            val interval = date.toDayInterval()
            when (val result = bookingInteractor.getBookings(
                roomId = room.id,
                date = dateKey,
                timeStart = interval.start,
                timeEnd = interval.end,
                forceRefresh = true
            )) {
                is Result.Success -> _uiState.update { it.copy(isBookingsRefreshing = false) }
                is Result.Error -> showBookingsError(result.message, userInitiated)
                is Result.Exception -> showBookingsError(
                    result.error.message ?: "Не удалось загрузить занятость",
                    userInitiated
                )
            }
        }
    }

    private fun showBookingsError(message: String, userInitiated: Boolean) {
        val hasContent = _uiState.value.bookings.isNotEmpty()
        _uiState.update {
            it.copy(
                isBookingsRefreshing = false,
                bookingsLoadState = if (hasContent) it.bookingsLoadState else BookingPostsLoadState.Error(message)
            )
        }
        if (userInitiated || hasContent) emitEffect(BookingScreenUiEffect.ShowMessage(message))
    }

    private fun GroupedRooms.toUiModel(): BookingRoomGroupUiModel =
        BookingRoomGroupUiModel(
            corpus = corpus,
            rooms = rooms
                .map { BookingRoomUiModel(id = it.id, name = it.roomName, corpus = it.corpus) }
                .sortedWith(compareBy<BookingRoomUiModel> { it.name.length }.thenBy { it.name })
                .toImmutableList()
        )

    private fun Booking.toUiModel(): BookingUiModel {
        val start = timeStart.toDisplayDateTime()
        val end = timeEnd.toDisplayDateTime()
        return BookingUiModel(
            id = id,
            intervalText = "$start - $end",
            description = description?.takeIf(String::isNotBlank) ?: "Без описания",
            roomName = roomName,
            authorTelegramId = author.telegramId,
            isMine = isMine
        )
    }

    private fun LocalDate.toDayInterval(): DayInterval {
        val start = LocalDateTime.of(this, LocalTime.MIN).toApiInstant()
        val end = LocalDateTime.of(plusDays(1), LocalTime.MIN).toApiInstant()
        return DayInterval(start = start, end = end)
    }

    private fun String.parseDateOrNull(): LocalDate? =
        try {
            LocalDate.parse(this, DATE_FORMATTER)
        } catch (_: DateTimeParseException) {
            null
        }

    private fun Instant.toDisplayDateTime(): String =
        toJavaInstant().atZone(UI_ZONE).toLocalDateTime().format(DISPLAY_FORMATTER)

    private fun LocalDateTime.toApiInstant(): Instant =
        atZone(UI_ZONE).toInstant().toKotlinInstant()

    private fun emitEffect(effect: BookingScreenUiEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }

    private data class DayInterval(val start: Instant, val end: Instant)

    private companion object {
        const val DATE_INPUT_LENGTH = 10
        val UI_ZONE: ZoneId = ZoneId.systemDefault()
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val DISPLAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")
    }
}
