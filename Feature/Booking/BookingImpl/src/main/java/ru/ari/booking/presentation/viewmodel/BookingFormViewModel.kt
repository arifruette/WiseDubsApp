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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.booking.domain.interactor.BookingInteractor
import ru.ari.booking.domain.models.Booking
import ru.ari.booking.domain.models.CreateBookingParams
import ru.ari.booking.domain.models.GroupedRooms
import ru.ari.booking.domain.models.UpdateBookingParams
import ru.ari.booking.presentation.contract.BookingFormAction
import ru.ari.booking.presentation.contract.BookingFormUiEffect
import ru.ari.booking.presentation.contract.BookingFormUiState
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

class BookingFormViewModel @Inject constructor(
    private val bookingInteractor: BookingInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(newInitialState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<BookingFormUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private var initialized = false
    private var initialArgs: BookingFormAction.Load? = null
    private var intersectionsJob: Job? = null
    private var intersectionsRequest: IntersectionsRequest? = null

    fun onAction(action: BookingFormAction) {
        when (action) {
            is BookingFormAction.Load -> load(action, force = false)
            BookingFormAction.Retry -> initialArgs?.let { load(it, force = true) }
            BookingFormAction.OpenCorpusSelector -> {
                if (_uiState.value.roomsLoadState == BookingRoomsLoadState.Content) {
                    _uiState.update { it.copy(activeRoomSheet = BookingRoomSheetStep.Corpus) }
                }
            }
            BookingFormAction.OpenRoomSelector -> {
                val state = _uiState.value
                if (state.roomsLoadState == BookingRoomsLoadState.Content && state.selectedCorpus.isNotBlank()) {
                    _uiState.update { it.copy(activeRoomSheet = BookingRoomSheetStep.Room) }
                }
            }
            BookingFormAction.DismissRoomSelector -> _uiState.update {
                it.copy(activeRoomSheet = BookingRoomSheetStep.None)
            }
            is BookingFormAction.SelectCorpus -> _uiState.update {
                it.copy(
                    selectedCorpus = action.corpus,
                    selectedRoom = null,
                    roomSearchQuery = "",
                    activeRoomSheet = BookingRoomSheetStep.None
                )
            }.also { checkIntersections() }
            is BookingFormAction.SelectRoom -> _uiState.update {
                it.copy(selectedRoom = action.room, activeRoomSheet = BookingRoomSheetStep.None)
            }.also { checkIntersections() }
            is BookingFormAction.ChangeRoomSearchQuery -> _uiState.update {
                it.copy(roomSearchQuery = action.value.take(40))
            }
            is BookingFormAction.ChangeDate -> updateStartDate(action.value.take(DATE_INPUT_LENGTH))
                .also { checkIntersections() }
            is BookingFormAction.ChangeStartTime -> updateStartTime(action.value.take(TIME_INPUT_LENGTH))
                .also { checkIntersections() }
            is BookingFormAction.ChangeEndDate -> _uiState.update {
                it.copy(endDate = action.value.take(DATE_INPUT_LENGTH))
            }.also { checkIntersections() }
            is BookingFormAction.ChangeEndTime -> _uiState.update {
                it.copy(endTime = action.value.take(TIME_INPUT_LENGTH))
            }.also { checkIntersections() }
            is BookingFormAction.ChangeDuration -> _uiState.update {
                it.copy(durationMinutes = action.value.filter(Char::isDigit).take(4))
            }.also { checkIntersections() }
            is BookingFormAction.ChangeTimeMode -> _uiState.update {
                it.copy(timeMode = action.mode).ensureValidEndDateTime()
            }.also { checkIntersections() }
            is BookingFormAction.ChangeDescription -> _uiState.update {
                it.copy(description = action.value.take(200))
            }
            BookingFormAction.ClickDelete -> {
                if (_uiState.value.isEditMode && !_uiState.value.isDeleting) {
                    _uiState.update { it.copy(showDeleteConfirmDialog = true) }
                }
            }
            BookingFormAction.DismissDelete -> {
                if (!_uiState.value.isDeleting) {
                    _uiState.update { it.copy(showDeleteConfirmDialog = false) }
                }
            }
            BookingFormAction.ConfirmDelete -> deleteBooking()
            BookingFormAction.Submit -> submit()
            BookingFormAction.ClickBack -> emitEffect(BookingFormUiEffect.NavigateBack)
        }
    }

    private fun load(args: BookingFormAction.Load, force: Boolean) {
        if (!force && initialized && args == initialArgs) return
        initialized = true
        initialArgs = args
        intersectionsJob?.cancel()
        intersectionsRequest = null
        _uiState.value = newInitialState(
            postId = args.postId,
            initialDate = args.initialDate
        )
        loadRooms(args.initialRoomId)
        if (args.postId != null) loadBooking(args.postId)
    }

    private fun loadRooms(initialRoomId: Int?) {
        _uiState.update { it.copy(roomsLoadState = BookingRoomsLoadState.Loading, loadError = null) }
        viewModelScope.launch {
            when (val result = bookingInteractor.getGroupedRooms()) {
                is Result.Success -> {
                    val groups = result.data.map { it.toUiModel() }.filter { it.rooms.isNotEmpty() }.toImmutableList()
                    val selectedRoomId = initialRoomId ?: _uiState.value.selectedRoom?.id
                    val initialRoom = selectedRoomId?.let { id ->
                        groups.flatMap { it.rooms }.firstOrNull { it.id == id }
                    }
                    _uiState.update {
                        it.copy(
                            roomGroups = groups,
                            roomsLoadState = if (groups.isEmpty()) BookingRoomsLoadState.Empty else BookingRoomsLoadState.Content,
                            selectedCorpus = initialRoom?.corpus ?: it.selectedRoom?.corpus ?: it.selectedCorpus,
                            selectedRoom = initialRoom ?: it.selectedRoom
                        )
                    }
                    checkIntersections()
                }
                is Result.Error -> _uiState.update {
                    it.copy(roomsLoadState = BookingRoomsLoadState.Error(result.message), loadError = result.message)
                }
                is Result.Exception -> _uiState.update {
                    val message = result.error.message ?: "Не удалось загрузить комнаты"
                    it.copy(roomsLoadState = BookingRoomsLoadState.Error(message), loadError = message)
                }
            }
        }
    }

    private fun loadBooking(postId: Long) {
        _uiState.update { it.copy(isBookingLoading = true, loadError = null) }
        viewModelScope.launch {
            when (val result = bookingInteractor.getBookingById(postId)) {
                is Result.Success -> applyBooking(result.data)
                is Result.Error -> _uiState.update { it.copy(isBookingLoading = false, loadError = result.message) }
                is Result.Exception -> _uiState.update {
                    it.copy(isBookingLoading = false, loadError = result.error.message ?: "Не удалось загрузить бронь")
                }
            }
        }
    }

    private fun applyBooking(booking: Booking) {
        val start = booking.timeStart.toLocalDateTime()
        val end = booking.timeEnd.toLocalDateTime()
        _uiState.update { state ->
            val room = state.roomGroups.flatMap { it.rooms }.firstOrNull { it.id == booking.roomId }
                ?: BookingRoomUiModel(booking.roomId, booking.roomName, state.selectedCorpus)
            state.copy(
                isBookingLoading = false,
                selectedCorpus = room.corpus,
                selectedRoom = room,
                selectedDate = start.toLocalDate().format(DATE_FORMATTER),
                startTime = start.toLocalTime().format(TIME_FORMATTER),
                endDate = end.toLocalDate().format(DATE_FORMATTER),
                endTime = end.toLocalTime().format(TIME_FORMATTER),
                durationMinutes = booking.duration.toString(),
                description = booking.description.orEmpty()
            )
        }
        checkIntersections()
    }

    private fun submit() {
        val state = _uiState.value
        if (!state.canSubmit) return
        val room = state.selectedRoom
        if (room == null) {
            emitEffect(BookingFormUiEffect.ShowMessage("Выберите комнату"))
            return
        }
        val interval = state.toNormalizedInterval()
        if (interval == null) {
            emitEffect(BookingFormUiEffect.ShowMessage("Проверьте дату, время и длительность"))
            return
        }

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val result = if (state.postId == null) {
                bookingInteractor.createBooking(
                    CreateBookingParams(
                        roomId = room.id,
                        timeStart = interval.start,
                        timeEnd = interval.end,
                        duration = interval.durationMinutes,
                        description = state.description.trim().ifBlank { null }
                    )
                )
            } else {
                bookingInteractor.updateBooking(
                    UpdateBookingParams(
                        postId = state.postId,
                        roomId = room.id,
                        timeStart = interval.start,
                        timeEnd = interval.end,
                        duration = interval.durationMinutes,
                        description = state.description.trim().ifBlank { null }
                    )
                )
            }
            when (result) {
                is Result.Success -> {
                    val isCreate = state.postId == null
                    _uiState.update { it.copy(isSubmitting = false) }
                    emitEffect(
                        BookingFormUiEffect.Completed(
                            isCreate = isCreate,
                            message = if (isCreate) "Бронь создана" else "Бронь обновлена"
                        )
                    )
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    emitEffect(BookingFormUiEffect.ShowMessage(result.message))
                }
                is Result.Exception -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    emitEffect(BookingFormUiEffect.ShowMessage(result.error.message ?: "Не удалось сохранить бронь"))
                }
            }
        }
    }

    private fun deleteBooking() {
        val postId = _uiState.value.postId ?: return
        if (_uiState.value.isDeleting) return

        _uiState.update { it.copy(isDeleting = true) }
        viewModelScope.launch {
            when (val result = bookingInteractor.deleteBooking(postId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            showDeleteConfirmDialog = false
                        )
                    }
                    emitEffect(
                        BookingFormUiEffect.Completed(
                            isCreate = false,
                            message = "Бронь удалена"
                        )
                    )
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            showDeleteConfirmDialog = true
                        )
                    }
                    emitEffect(BookingFormUiEffect.ShowMessage(result.message))
                }
                is Result.Exception -> {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            showDeleteConfirmDialog = true
                        )
                    }
                    emitEffect(BookingFormUiEffect.ShowMessage(result.error.message ?: "Не удалось удалить бронь"))
                }
            }
        }
    }

    private fun checkIntersections() {
        val state = _uiState.value
        val room = state.selectedRoom
        val interval = state.toNormalizedInterval()
        if (room == null || interval == null) {
            clearIntersections()
            return
        }
        val request = IntersectionsRequest(
            roomId = room.id,
            timeStart = interval.start,
            timeEnd = interval.end,
            postId = state.postId
        )
        if (request == intersectionsRequest) return

        intersectionsJob?.cancel()
        intersectionsRequest = request
        _uiState.update {
            it.copy(
                isIntersectionsLoading = true,
                intersections = emptyList<BookingUiModel>().toImmutableList()
            )
        }
        intersectionsJob = viewModelScope.launch {
            delay(INTERSECTIONS_CHECK_DELAY_MS)
            when (val result = bookingInteractor.getBookingIntersections(
                roomId = request.roomId,
                timeStart = request.timeStart,
                timeEnd = request.timeEnd
            )) {
                is Result.Success -> {
                    if (intersectionsRequest != request) return@launch
                    val intersections = result.data
                        .filter { it.id != request.postId }
                        .map { it.toUiModel() }
                        .toImmutableList()
                    _uiState.update {
                        it.copy(
                            isIntersectionsLoading = false,
                            intersections = intersections
                        )
                    }
                }
                is Result.Error -> {
                    if (intersectionsRequest != request) return@launch
                    showIntersectionsError(result.message)
                }
                is Result.Exception -> showIntersectionsError(
                    message = result.error.message ?: "Не удалось проверить пересечения",
                    request = request
                )
            }
        }
    }

    private fun clearIntersections() {
        intersectionsJob?.cancel()
        intersectionsRequest = null
        _uiState.update {
            it.copy(
                isIntersectionsLoading = false,
                intersections = emptyList<BookingUiModel>().toImmutableList()
            )
        }
    }

    private fun showIntersectionsError(message: String, request: IntersectionsRequest? = null) {
        if (request != null && intersectionsRequest != request) return
        intersectionsRequest = null
        _uiState.update {
            it.copy(
                isIntersectionsLoading = false,
                intersections = emptyList<BookingUiModel>().toImmutableList()
            )
        }
        emitEffect(BookingFormUiEffect.ShowMessage(message))
    }

    private fun updateStartDate(value: String) {
        _uiState.update { it.copy(selectedDate = value).ensureValidEndDateTime() }
    }

    private fun updateStartTime(value: String) {
        _uiState.update { it.copy(startTime = value).ensureValidEndDateTime() }
    }

    private fun BookingFormUiState.ensureValidEndDateTime(): BookingFormUiState {
        if (timeMode != BookingTimeMode.EndTime) return this
        val startDate = selectedDate.parseDateOrNull() ?: return this
        val startTimeValue = startTime.parseTimeOrNull() ?: return this
        val start = LocalDateTime.of(startDate, startTimeValue)
        val end = endDate.parseDateOrNull()?.let { date ->
            endTime.parseTimeOrNull()?.let { time -> LocalDateTime.of(date, time) }
        }
        if (end != null && end.isAfter(start)) return this
        val nextEnd = start.plusMinutes(DEFAULT_DURATION_MINUTES.toLong())
        return copy(
            endDate = nextEnd.toLocalDate().format(DATE_FORMATTER),
            endTime = nextEnd.toLocalTime().format(TIME_FORMATTER)
        )
    }

    private fun BookingFormUiState.toNormalizedInterval(): NormalizedInterval? {
        val startDate = selectedDate.parseDateOrNull() ?: return null
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
        if (!end.isAfter(start)) return null
        val apiStart = start.toApiInstant()
        val apiEnd = end.toApiInstant()
        val duration = Duration.between(apiStart.toJavaInstant(), apiEnd.toJavaInstant()).toMinutes()
        if (duration <= 0 || duration > Int.MAX_VALUE) return null
        return NormalizedInterval(apiStart, apiEnd, duration.toInt())
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
        val start = timeStart.toLocalDateTime()
        val end = timeEnd.toLocalDateTime()
        val intervalText = if (start.toLocalDate() == end.toLocalDate()) {
            "${start.toLocalTime().format(TIME_FORMATTER)}-${end.toLocalTime().format(TIME_FORMATTER)}"
        } else {
            "${start.format(DISPLAY_FORMATTER)} - ${end.format(DISPLAY_FORMATTER)}"
        }
        return BookingUiModel(
            id = id,
            intervalText = intervalText,
            description = description?.takeIf(String::isNotBlank) ?: "Без описания",
            roomName = roomName,
            authorTelegramId = author.telegramId,
            isMine = isMine
        )
    }

    private fun newInitialState(
        postId: Long? = null,
        initialDate: String? = null
    ): BookingFormUiState {
        val start = LocalDateTime.of(
            initialDate?.parseDateOrNull() ?: LocalDate.now(),
            LocalTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0)
        )
        val end = start.plusHours(1)
        return BookingFormUiState(
            postId = postId,
            selectedDate = start.toLocalDate().format(DATE_FORMATTER),
            startTime = start.toLocalTime().format(TIME_FORMATTER),
            endDate = end.toLocalDate().format(DATE_FORMATTER),
            endTime = end.toLocalTime().format(TIME_FORMATTER)
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

    private fun Instant.toLocalDateTime(): LocalDateTime =
        toJavaInstant().atZone(UI_ZONE).toLocalDateTime()

    private fun LocalDateTime.toApiInstant(): Instant =
        atZone(UI_ZONE).toInstant().toKotlinInstant()

    private fun emitEffect(effect: BookingFormUiEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }

    private data class NormalizedInterval(
        val start: Instant,
        val end: Instant,
        val durationMinutes: Int
    )

    private data class IntersectionsRequest(
        val roomId: Int,
        val timeStart: Instant,
        val timeEnd: Instant,
        val postId: Long?
    )

    private companion object {
        const val DATE_INPUT_LENGTH = 10
        const val TIME_INPUT_LENGTH = 5
        const val DEFAULT_DURATION_MINUTES = 60
        const val INTERSECTIONS_CHECK_DELAY_MS = 350L
        val UI_ZONE: ZoneId = ZoneId.systemDefault()
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val DISPLAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")
    }
}
