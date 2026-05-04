package ru.ari.booking.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.roundToInt
import ru.ari.booking.presentation.contract.BookingActionHandler
import ru.ari.booking.presentation.contract.BookingScreenAction
import ru.ari.booking.presentation.contract.BookingScreenUiState
import ru.ari.booking.presentation.models.BookingRoomSheetStep
import ru.ari.booking.presentation.models.BookingRoomsLoadState
import ru.ari.booking.presentation.models.BookingTimeMode
import ru.ari.designsystem.components.WiseDubsProgressIndicator
import ru.ari.designsystem.components.WiseDubsStableTextField
import ru.ari.designsystem.components.WiseDubsTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    uiState: BookingScreenUiState,
    actionHandler: BookingActionHandler,
    scrollToBookingsRequest: Int,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            WiseDubsTopAppBar(title = "Бронирование")
        },
        bottomBar = {
            if (uiState.roomsLoadState == BookingRoomsLoadState.Content) {
                BookingSubmitBar(
                    isSubmitting = uiState.isSubmitting,
                    onSubmitClick = { actionHandler.onAction(BookingScreenAction.Submit) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.roomsLoadState) {
                BookingRoomsLoadState.Loading -> WiseDubsProgressIndicator()
                BookingRoomsLoadState.Empty -> EmptyState(
                    title = "Комнат пока нет",
                    onRetryClick = { actionHandler.onAction(BookingScreenAction.RetryRooms) }
                )

                is BookingRoomsLoadState.Error -> EmptyState(
                    title = "Не удалось загрузить комнаты",
                    onRetryClick = { actionHandler.onAction(BookingScreenAction.RetryRooms) }
                )

                BookingRoomsLoadState.Content -> BookingContent(
                    uiState = uiState,
                    actionHandler = actionHandler,
                    scrollToBookingsRequest = scrollToBookingsRequest
                )
            }
        }

        when (uiState.activeRoomSheet) {
            BookingRoomSheetStep.None -> Unit
            BookingRoomSheetStep.Corpus -> ModalBottomSheet(
                onDismissRequest = {
                    actionHandler.onAction(BookingScreenAction.DismissRoomSelector)
                }
            ) {
                CorpusSheet(
                    roomGroups = uiState.roomGroups,
                    selectedCorpus = uiState.selectedCorpus,
                    onCorpusClick = {
                        actionHandler.onAction(BookingScreenAction.SelectCorpus(it))
                    }
                )
            }

            BookingRoomSheetStep.Room -> ModalBottomSheet(
                onDismissRequest = {
                    actionHandler.onAction(BookingScreenAction.DismissRoomSelector)
                }
            ) {
                RoomSheet(
                    corpus = uiState.selectedCorpus,
                    query = uiState.roomSearchQuery,
                    rooms = uiState.roomGroups.firstOrNull { it.corpus == uiState.selectedCorpus }?.rooms,
                    selectedRoomId = uiState.selectedRoom?.id,
                    onQueryChange = {
                        actionHandler.onAction(BookingScreenAction.ChangeRoomSearchQuery(it))
                    },
                    onRoomClick = {
                        actionHandler.onAction(BookingScreenAction.SelectRoom(it))
                    }
                )
            }
        }
    }
}

@Composable
private fun BookingContent(
    uiState: BookingScreenUiState,
    actionHandler: BookingActionHandler,
    scrollToBookingsRequest: Int,
    modifier: Modifier = Modifier
) {
    val enabled = !uiState.isSubmitting
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    var bookingsOffset by remember { mutableIntStateOf(0) }
    var activePicker by rememberSaveable { mutableStateOf<BookingPicker?>(null) }

    LaunchedEffect(scrollToBookingsRequest) {
        if (scrollToBookingsRequest > 0) {
            val topPadding = with(density) { 16.dp.toPx() }.roundToInt()
            scrollState.animateScrollTo((bookingsOffset - topPadding).coerceAtLeast(0))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .padding(top = 20.dp, bottom = 92.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        RoomSelectorSection(
            selectedCorpus = uiState.selectedCorpus,
            selectedRoom = uiState.selectedRoom,
            enabled = enabled,
            onCorpusClick = { actionHandler.onAction(BookingScreenAction.OpenCorpusSelector) },
            onRoomClick = { actionHandler.onAction(BookingScreenAction.OpenRoomSelector) }
        )

        DateTimeSelectionRow(
            dateLabel = "Дата начала",
            dateValue = uiState.selectedDate,
            timeLabel = "Время начала",
            timeValue = uiState.startTime,
            enabled = enabled,
            onDateClick = { activePicker = BookingPicker.StartDate },
            onTimeClick = { activePicker = BookingPicker.StartTime }
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = uiState.timeMode == BookingTimeMode.EndTime,
                onClick = { actionHandler.onAction(BookingScreenAction.ChangeTimeMode(BookingTimeMode.EndTime)) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                enabled = enabled
            ) {
                Text("По окончанию")
            }
            SegmentedButton(
                selected = uiState.timeMode == BookingTimeMode.Duration,
                onClick = { actionHandler.onAction(BookingScreenAction.ChangeTimeMode(BookingTimeMode.Duration)) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                enabled = enabled
            ) {
                Text("По длительности")
            }
        }

        if (uiState.timeMode == BookingTimeMode.EndTime) {
            DateTimeSelectionRow(
                dateLabel = "Дата конца",
                dateValue = uiState.endDate,
                timeLabel = "Время конца",
                timeValue = uiState.endTime,
                enabled = enabled,
                onDateClick = { activePicker = BookingPicker.EndDate },
                onTimeClick = { activePicker = BookingPicker.EndTime }
            )
        } else {
            WiseDubsStableTextField(
                value = uiState.durationMinutes,
                onValueChanged = { actionHandler.onAction(BookingScreenAction.ChangeDuration(it)) },
                labelText = "Длительность, минут",
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }

        WiseDubsStableTextField(
            value = uiState.description,
            onValueChanged = { actionHandler.onAction(BookingScreenAction.ChangeDescription(it)) },
            labelText = "Описание",
            enabled = enabled,
            minLines = 2,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        BookingsSection(
            loadState = uiState.bookingsLoadState,
            bookings = uiState.bookings,
            hasSelectedRoom = uiState.selectedRoom != null,
            onRetryClick = { actionHandler.onAction(BookingScreenAction.RetryBookings) },
            modifier = Modifier.onGloballyPositioned { coordinates ->
                bookingsOffset = scrollState.value + coordinates.positionInParent().y.roundToInt()
            }
        )
    }

    when (activePicker) {
        BookingPicker.StartDate -> BookingDatePickerDialog(
            value = uiState.selectedDate,
            minDate = LocalDate.now(),
            onDismiss = { activePicker = null },
            onDateSelected = {
                actionHandler.onAction(BookingScreenAction.ChangeDate(it))
                activePicker = null
            }
        )

        BookingPicker.StartTime -> BookingTimePickerDialog(
            title = "Время начала",
            value = uiState.startTime,
            onDismiss = { activePicker = null },
            onTimeSelected = {
                actionHandler.onAction(BookingScreenAction.ChangeStartTime(it))
                activePicker = null
            }
        )

        BookingPicker.EndDate -> BookingDatePickerDialog(
            value = uiState.endDate,
            minDate = uiState.selectedDate.toLocalDateOrNull() ?: LocalDate.now(),
            onDismiss = { activePicker = null },
            onDateSelected = {
                actionHandler.onAction(BookingScreenAction.ChangeEndDate(it))
                activePicker = null
            }
        )

        BookingPicker.EndTime -> BookingTimePickerDialog(
            title = "Время конца",
            value = uiState.endTime,
            onDismiss = { activePicker = null },
            onTimeSelected = {
                actionHandler.onAction(BookingScreenAction.ChangeEndTime(it))
                activePicker = null
            }
        )

        null -> Unit
    }
}

@Composable
private fun DateTimeSelectionRow(
    dateLabel: String,
    dateValue: String,
    timeLabel: String,
    timeValue: String,
    enabled: Boolean,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BookingSelectionField(
            label = dateLabel,
            value = dateValue.toDisplayBookingDate(),
            placeholder = "Выбрать дату",
            enabled = enabled,
            onClick = onDateClick,
            fieldHeight = 86.dp,
            valueMaxLines = 1,
            modifier = Modifier.weight(1f)
        )
        BookingSelectionField(
            label = timeLabel,
            value = timeValue,
            placeholder = "Выбрать время",
            enabled = enabled,
            onClick = onTimeClick,
            fieldHeight = 86.dp,
            valueMaxLines = 1,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BookingSubmitBar(
    isSubmitting: Boolean,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Button(
            onClick = onSubmitClick,
            enabled = !isSubmitting,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.height(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Создать бронь", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun EmptyState(
    title: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onRetryClick) {
            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
            Text("Повторить")
        }
    }
}

private enum class BookingPicker {
    StartDate,
    StartTime,
    EndDate,
    EndTime
}

private fun String.toDisplayBookingDate(): String =
    try {
        LocalDate.parse(this).format(DISPLAY_DATE_FORMATTER)
    } catch (_: DateTimeParseException) {
        this
    }

private val DISPLAY_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy")
