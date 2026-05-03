@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.Duration
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
import ru.ari.booking.domain.models.CreateBookingParams
import ru.ari.booking.domain.models.GroupedRooms
import ru.ari.booking.presentation.contract.BookingScreenAction
import ru.ari.booking.presentation.contract.BookingScreenUiEffect
import ru.ari.booking.presentation.contract.BookingScreenUiState
import ru.ari.booking.presentation.models.BookingPostsLoadState
import ru.ari.booking.presentation.models.BookingRoomGroupUiModel
import ru.ari.booking.presentation.models.BookingRoomSheetStep
import ru.ari.booking.presentation.models.BookingRoomUiModel
import ru.ari.booking.presentation.models.BookingRoomsLoadState
import ru.ari.booking.presentation.models.BookingTimeMode
import ru.ari.booking.presentation.models.BookingUiModel
import ru.ari.network.domain.models.Result
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

class BookingViewModel @Inject constructor(
    private val bookingInteractor: BookingInteractor
) : ViewModel() {

    private val initialStartDateTime = LocalDateTime.of(
        LocalDate.now(),
        LocalTime.now()
            .plusHours(1)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
    )
    private val initialEndDateTime = initialStartDateTime.plusHours(1)

    private val _uiState = MutableStateFlow(
        BookingScreenUiState(
            selectedDate = initialStartDateTime.toLocalDate().format(DATE_FORMATTER),
            startTime = initialStartDateTime.toLocalTime().format(TIME_FORMATTER),
            endDate = initialEndDateTime.toLocalDate().format(DATE_FORMATTER),
            endTime = initialEndDateTime.toLocalTime().format(TIME_FORMATTER)
        )
    )
    val uiState: StateFlow<BookingScreenUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<BookingScreenUiEffect>()
    val uiEffect: SharedFlow<BookingScreenUiEffect> = _uiEffect.asSharedFlow()

    private var roomsLoaded = false
    private var bookingsLoadJob: Job? = null
    private var bookingsRequestId = 0L

    fun onAction(action: BookingScreenAction) {
        when (action) {
            BookingScreenAction.Load -> {
                if (!roomsLoaded) {
                    loadRooms()
                }
            }

            BookingScreenAction.RetryRooms -> loadRooms()
            BookingScreenAction.RetryBookings -> refreshBookings()
            BookingScreenAction.OpenCorpusSelector -> {
                if (_uiState.value.roomsLoadState == BookingRoomsLoadState.Content) {
                    _uiState.update { it.copy(activeRoomSheet = BookingRoomSheetStep.Corpus) }
                }
            }

            BookingScreenAction.OpenRoomSelector -> {
                val state = _uiState.value
                if (
                    state.roomsLoadState == BookingRoomsLoadState.Content &&
                    state.selectedCorpus.isNotBlank()
                ) {
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
                        activeRoomSheet = BookingRoomSheetStep.None
                    ).withoutBookings()
                }
            }

            is BookingScreenAction.SelectRoom -> {
                _uiState.update {
                    it.copy(
                        selectedRoom = action.room,
                        activeRoomSheet = BookingRoomSheetStep.None
                    )
                }
                refreshBookings()
            }

            is BookingScreenAction.ChangeRoomSearchQuery -> {
                _uiState.update { it.copy(roomSearchQuery = action.value.take(40)) }
            }

            is BookingScreenAction.ChangeDate -> {
                updateStartDate(action.value.take(DATE_INPUT_LENGTH))
                refreshBookingsIfPossible()
            }

            is BookingScreenAction.ChangeStartTime -> {
                updateStartTime(action.value.take(TIME_INPUT_LENGTH))
                refreshBookingsIfPossible()
            }

            is BookingScreenAction.ChangeEndDate -> {
                _uiState.update { it.copy(endDate = action.value.take(DATE_INPUT_LENGTH)) }
                refreshBookingsIfPossible()
            }

            is BookingScreenAction.ChangeEndTime -> {
                _uiState.update { it.copy(endTime = action.value.take(TIME_INPUT_LENGTH)) }
                refreshBookingsIfPossible()
            }

            is BookingScreenAction.ChangeDuration -> {
                _uiState.update { it.copy(durationMinutes = action.value.filter(Char::isDigit).take(4)) }
                refreshBookingsIfPossible()
            }

            is BookingScreenAction.ChangeTimeMode -> {
                _uiState.update { state ->
                    state.copy(timeMode = action.mode).ensureValidEndDateTime()
                }
                refreshBookingsIfPossible()
            }

            is BookingScreenAction.ChangeDescription -> {
                _uiState.update { it.copy(description = action.value.take(200)) }
            }

            BookingScreenAction.Submit -> submit()
        }
    }

    private fun loadRooms() {
        _uiState.update {
            it.copy(
                roomsLoadState = BookingRoomsLoadState.Loading,
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
                            roomsLoadState = if (groups.isEmpty()) {
                                BookingRoomsLoadState.Empty
                            } else {
                                BookingRoomsLoadState.Content
                            }
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(roomsLoadState = BookingRoomsLoadState.Error(result.message))
                    }
                }

                is Result.Exception -> {
                    _uiState.update {
                        it.copy(
                            roomsLoadState = BookingRoomsLoadState.Error(
                                result.error.message ?: "Не удалось загрузить комнаты"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun refreshBookingsIfPossible() {
        if (_uiState.value.selectedRoom != null) {
            refreshBookings()
        }
    }

    private fun refreshBookings() {
        val state = _uiState.value
        val room = state.selectedRoom
        if (room == null) {
            bookingsLoadJob?.cancel()
            _uiState.update {
                it.copy(
                    bookingsLoadState = BookingPostsLoadState.Idle,
                    bookings = emptyList<BookingUiModel>().toImmutableList()
                )
            }
            return
        }
        val interval = state.toNormalizedInterval()
        if (interval == null) {
            bookingsLoadJob?.cancel()
            _uiState.update {
                it.copy(
                    bookingsLoadState = BookingPostsLoadState.Idle,
                    bookings = emptyList<BookingUiModel>().toImmutableList()
                )
            }
            return
        }

        bookingsLoadJob?.cancel()
        val requestId = ++bookingsRequestId
        _uiState.update { it.copy(bookingsLoadState = BookingPostsLoadState.Loading) }
        bookingsLoadJob = viewModelScope.launch {
            when (val result = bookingInteractor.getBookings(
                roomId = room.id,
                timeStart = interval.start,
                timeEnd = interval.end
            )) {
                is Result.Success -> {
                    if (requestId != bookingsRequestId) {
                        return@launch
                    }
                    val bookings = result.data
                        .sortedBy { it.timeStart }
                        .map { it.toUiModel() }
                        .toImmutableList()
                    _uiState.update {
                        it.copy(
                            bookingsLoadState = if (bookings.isEmpty()) {
                                BookingPostsLoadState.Empty
                            } else {
                                BookingPostsLoadState.Content
                            },
                            bookings = bookings
                        )
                    }
                }

                is Result.Error -> {
                    if (requestId != bookingsRequestId) {
                        return@launch
                    }
                    _uiState.update {
                        it.copy(bookingsLoadState = BookingPostsLoadState.Error(result.message))
                    }
                }

                is Result.Exception -> {
                    if (requestId != bookingsRequestId) {
                        return@launch
                    }
                    _uiState.update {
                        it.copy(
                            bookingsLoadState = BookingPostsLoadState.Error(
                                result.error.message ?: "Не удалось загрузить занятость"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun submit() {
        val state = _uiState.value
        val room = state.selectedRoom
        if (room == null) {
            emitEffect(BookingScreenUiEffect.ShowMessage("Выберите комнату"))
            return
        }

        val interval = state.toNormalizedInterval()
        if (interval == null) {
            emitEffect(BookingScreenUiEffect.ShowMessage("Проверьте дату, время и длительность"))
            return
        }

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val result = bookingInteractor.createBooking(
                CreateBookingParams(
                    roomId = room.id,
                    timeStart = interval.start,
                    timeEnd = interval.end,
                    duration = interval.durationMinutes,
                    description = state.description.trim().ifBlank { null }
                )
            )

            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    emitEffect(BookingScreenUiEffect.ShowMessage("Бронь создана"))
                    refreshBookings()
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    emitEffect(
                        BookingScreenUiEffect.ShowMessage(
                            if (result.code == 409) {
                                emitEffect(BookingScreenUiEffect.ScrollToBookings)
                                "Комната уже занята на выбранное время"
                            } else {
                                result.message
                            }
                        )
                    )
                }

                is Result.Exception -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    emitEffect(
                        BookingScreenUiEffect.ShowMessage(
                            result.error.message ?: "Не удалось создать бронь"
                        )
                    )
                }
            }
        }
    }

    private fun updateStartDate(value: String) {
        _uiState.update { state ->
            state.copy(selectedDate = value.coerceStartDate()).ensureValidEndDateTime()
        }
    }

    private fun updateStartTime(value: String) {
        _uiState.update { state ->
            state.copy(startTime = value).ensureValidEndDateTime()
        }
    }

    private fun BookingScreenUiState.ensureValidEndDateTime(): BookingScreenUiState {
        if (timeMode != BookingTimeMode.EndTime) {
            return this
        }

        val startDate = selectedDate.parseDateOrNull() ?: return this
        val startTimeValue = startTime.parseTimeOrNull() ?: return this
        val start = LocalDateTime.of(startDate, startTimeValue)
        val currentEnd = endDate.parseDateOrNull()
            ?.let { date ->
                endTime.parseTimeOrNull()?.let { time -> LocalDateTime.of(date, time) }
            }

        if (currentEnd != null && currentEnd.isAfter(start)) {
            return this
        }

        val nextEnd = start.plusMinutes(DEFAULT_DURATION_MINUTES.toLong())
        return copy(
            endDate = nextEnd.toLocalDate().format(DATE_FORMATTER),
            endTime = nextEnd.toLocalTime().format(TIME_FORMATTER)
        )
    }

    private fun BookingScreenUiState.toNormalizedInterval(): NormalizedInterval? {
        val startDate = selectedDate.parseDateOrNull() ?: return null
        if (startDate.isBefore(LocalDate.now())) {
            return null
        }
        val start = LocalDateTime.of(startDate, startTime.parseTimeOrNull() ?: return null)
        val end = when (timeMode) {
            BookingTimeMode.EndTime -> {
                val parsedEndDate = endDate.parseDateOrNull() ?: return null
                LocalDateTime.of(parsedEndDate, endTime.parseTimeOrNull() ?: return null)
            }

            BookingTimeMode.Duration -> {
                val minutes = durationMinutes.toIntOrNull()?.takeIf { it > 0 } ?: return null
                start.plusMinutes(minutes.toLong())
            }
        }
        if (!end.isAfter(start)) {
            return null
        }
        val apiStart = start.toApiInstant()
        val apiEnd = end.toApiInstant()
        val duration = Duration.between(apiStart.toJavaInstant(), apiEnd.toJavaInstant()).toMinutes()
        if (duration <= 0 || duration > Int.MAX_VALUE) {
            return null
        }
        return NormalizedInterval(
            start = apiStart,
            end = apiEnd,
            durationMinutes = duration.toInt()
        )
    }

    private fun GroupedRooms.toUiModel(): BookingRoomGroupUiModel =
        BookingRoomGroupUiModel(
            corpus = corpus,
            rooms = rooms
                .map { room ->
                    BookingRoomUiModel(
                        id = room.id,
                        name = room.roomName,
                        corpus = room.corpus
                    )
                }
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
            roomName = roomName
        )
    }

    private fun String.parseDateOrNull(): LocalDate? =
        try {
            LocalDate.parse(this, DATE_FORMATTER)
        } catch (_: DateTimeParseException) {
            null
        }

    private fun String.parseTimeOrNull(): LocalTime? =
        try {
            LocalTime.parse(this, TIME_FORMATTER)
        } catch (_: DateTimeParseException) {
            null
        }

    private fun String.coerceStartDate(): String {
        val parsedDate = parseDateOrNull() ?: return this
        val today = LocalDate.now()
        return if (parsedDate.isBefore(today)) {
            today.format(DATE_FORMATTER)
        } else {
            this
        }
    }

    private fun BookingScreenUiState.withoutBookings(): BookingScreenUiState =
        copy(
            bookingsLoadState = BookingPostsLoadState.Idle,
            bookings = emptyList<BookingUiModel>().toImmutableList()
        )

    private fun Instant.toDisplayDateTime(): String =
        toJavaInstant()
            .atZone(UI_ZONE)
            .toLocalDateTime()
            .format(DISPLAY_FORMATTER)

    private fun LocalDateTime.toApiInstant(): Instant =
        atZone(UI_ZONE)
            .toInstant()
            .toKotlinInstant()

    private fun emitEffect(effect: BookingScreenUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }

    private data class NormalizedInterval(
        val start: Instant,
        val end: Instant,
        val durationMinutes: Int
    )

    private companion object {
        const val DATE_INPUT_LENGTH = 10
        const val TIME_INPUT_LENGTH = 5
        const val DEFAULT_DURATION_MINUTES = 60
        val UI_ZONE: ZoneId = ZoneId.systemDefault()
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val DISPLAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")
    }
}
