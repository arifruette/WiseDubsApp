@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.booking.domain.interactor.BookingInteractor
import ru.ari.booking.domain.models.Booking
import ru.ari.booking.domain.models.MyBookingsPeriod
import ru.ari.booking.presentation.contract.MyBookingsAction
import ru.ari.booking.presentation.contract.MyBookingsUiEffect
import ru.ari.booking.presentation.contract.MyBookingsUiState
import ru.ari.booking.presentation.models.BookingPostsLoadState
import ru.ari.booking.presentation.models.BookingUiModel
import ru.ari.network.domain.models.Result
import ru.ari.network.domain.models.toUserErrorMessage
import kotlin.time.Instant
import kotlin.time.toJavaInstant

class MyBookingsViewModel @Inject constructor(
    private val bookingInteractor: BookingInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyBookingsUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MyBookingsUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private var observeJob: Job? = null
    private var refreshJob: Job? = null
    private var hasStartedInitialLoad = false
    private var loadedPeriods: Set<MyBookingsPeriod> = emptySet()

    fun onAction(action: MyBookingsAction) {
        when (action) {
            MyBookingsAction.Load -> {
                if (!hasStartedInitialLoad) {
                    hasStartedInitialLoad = true
                    observePeriod(_uiState.value.selectedPeriod)
                }
                refresh(userInitiated = false)
            }
            MyBookingsAction.Refresh -> refresh(userInitiated = true)
            is MyBookingsAction.SelectPeriod -> {
                if (_uiState.value.selectedPeriod != action.period) {
                    _uiState.update {
                        it.copy(
                            selectedPeriod = action.period,
                            loadState = BookingPostsLoadState.Loading,
                            bookings = emptyList<BookingUiModel>().toImmutableList()
                        )
                    }
                    observePeriod(action.period)
                    refresh(userInitiated = false)
                }
            }
            is MyBookingsAction.ClickEdit -> emitEffect(MyBookingsUiEffect.OpenEditBooking(action.bookingId))
            MyBookingsAction.ClickBack -> emitEffect(MyBookingsUiEffect.NavigateBack)
        }
    }

    private fun observePeriod(period: MyBookingsPeriod) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            bookingInteractor.observeMyBookings(period).collect { bookings ->
                val uiBookings = bookings.sortedFor(period).map { it.toUiModel() }.toImmutableList()
                _uiState.update { state ->
                    state.copy(
                        bookings = uiBookings,
                        loadState = when {
                            uiBookings.isNotEmpty() -> BookingPostsLoadState.Content
                            period in loadedPeriods -> BookingPostsLoadState.Empty
                            state.loadState == BookingPostsLoadState.Loading -> BookingPostsLoadState.Loading
                            else -> BookingPostsLoadState.Empty
                        },
                        isRefreshing = false
                    )
                }
            }
        }
    }

    private fun refresh(userInitiated: Boolean) {
        refreshJob?.cancel()
        val requestedPeriod = _uiState.value.selectedPeriod
        val hasContent = _uiState.value.bookings.isNotEmpty()
        val hasLoadedPeriod = requestedPeriod in loadedPeriods
        _uiState.update {
            it.copy(
                loadState = if (!hasContent && !hasLoadedPeriod) BookingPostsLoadState.Loading else it.loadState,
                isRefreshing = userInitiated && (hasContent || hasLoadedPeriod)
            )
        }
        refreshJob = viewModelScope.launch {
            try {
                when (val result = bookingInteractor.getMyBookings(requestedPeriod, forceRefresh = true)) {
                    is Result.Success -> {
                        loadedPeriods += requestedPeriod
                        if (isSelectedPeriod(requestedPeriod)) {
                            _uiState.update { state ->
                                state.copy(
                                    isRefreshing = false,
                                    loadState = state.loadedStateFor(requestedPeriod)
                                )
                            }
                        }
                    }
                    is Result.Error -> {
                        if (isSelectedPeriod(requestedPeriod)) showLoadError(result.message, userInitiated)
                    }
                    is Result.Exception -> {
                        if (isSelectedPeriod(requestedPeriod)) {
                            showLoadError(result.error.toUserErrorMessage("Не удалось загрузить брони"), userInitiated)
                        }
                    }
                }
            } finally {
                if (refreshJob == currentCoroutineContext()[Job]) {
                    refreshJob = null
                }
            }
        }
    }

    private fun MyBookingsUiState.loadedStateFor(period: MyBookingsPeriod): BookingPostsLoadState = when {
        bookings.isNotEmpty() -> BookingPostsLoadState.Content
        period in loadedPeriods -> BookingPostsLoadState.Empty
        loadState == BookingPostsLoadState.Loading -> BookingPostsLoadState.Loading
        else -> BookingPostsLoadState.Empty
    }

    private fun isSelectedPeriod(period: MyBookingsPeriod): Boolean =
        _uiState.value.selectedPeriod == period

    private fun showLoadError(message: String, userInitiated: Boolean) {
        val hasContent = _uiState.value.bookings.isNotEmpty()
        _uiState.update {
            it.copy(
                isRefreshing = false,
                loadState = if (hasContent) it.loadState else BookingPostsLoadState.Error(message)
            )
        }
        if (userInitiated || hasContent) emitEffect(MyBookingsUiEffect.ShowMessage(message))
    }

    private fun Booking.toUiModel(): BookingUiModel = BookingUiModel(
        id = id,
        intervalText = "${timeStart.toDisplayDateTime()} - ${timeEnd.toDisplayDateTime()}",
        description = description?.takeIf(String::isNotBlank) ?: "Без описания",
        roomName = roomName,
        authorTelegramId = author.telegramId,
        isMine = isMine
    )

    private fun Instant.toDisplayDateTime(): String =
        toJavaInstant().atZone(UI_ZONE).toLocalDateTime().format(DISPLAY_FORMATTER)

    private fun List<Booking>.sortedFor(period: MyBookingsPeriod): List<Booking> = when (period) {
        MyBookingsPeriod.Upcoming -> sortedBy { it.timeStart }
        MyBookingsPeriod.Past -> sortedByDescending { it.timeStart }
    }

    private fun emitEffect(effect: MyBookingsUiEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }

    private companion object {
        val UI_ZONE: ZoneId = ZoneId.systemDefault()
        val DISPLAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")
    }
}
