package ru.ari.booking.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.ari.booking.presentation.contract.BookingFormAction
import ru.ari.booking.presentation.contract.BookingFormActionHandler
import ru.ari.booking.presentation.contract.BookingFormUiState
import ru.ari.booking.presentation.models.BookingRoomSheetStep
import ru.ari.booking.presentation.models.BookingRoomsLoadState
import ru.ari.booking.presentation.models.BookingTimeMode
import ru.ari.booking.presentation.models.BookingUiModel
import ru.ari.designsystem.components.WiseDubsProgressIndicator
import ru.ari.designsystem.components.WiseDubsStableTextField
import ru.ari.designsystem.components.WiseDubsTopAppBar
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    uiState: BookingFormUiState,
    actionHandler: BookingFormActionHandler,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = uiState.isDeleting) {
        // Keep the edit screen alive while the destructive request is in flight.
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            WiseDubsTopAppBar(
                title = if (uiState.isEditMode) "Редактирование брони" else "Новая бронь",
                onBackClick = if (uiState.isDeleting) {
                    null
                } else {
                    { actionHandler.onAction(BookingFormAction.ClickBack) }
                },
                actions = {
                    if (uiState.isEditMode) {
                        IconButton(
                            onClick = { actionHandler.onAction(BookingFormAction.ClickDelete) },
                            enabled = !uiState.isSubmitting && !uiState.isDeleting
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Удалить бронь"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.roomsLoadState == BookingRoomsLoadState.Content && !uiState.isBookingLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = { actionHandler.onAction(BookingFormAction.Submit) },
                        enabled = uiState.canSubmit,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (uiState.isEditMode) "Сохранить" else "Создать бронь",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        when {
            uiState.isBookingLoading || uiState.roomsLoadState == BookingRoomsLoadState.Loading -> {
                WiseDubsProgressIndicator(modifier = Modifier.padding(innerPadding))
            }
            uiState.loadError != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.loadError,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { actionHandler.onAction(BookingFormAction.Retry) }) {
                            Text("Повторить")
                        }
                    }
                }
            }
            else -> BookingFormContent(
                uiState = uiState,
                actionHandler = actionHandler,
                modifier = Modifier.padding(innerPadding)
            )
        }

        when (uiState.activeRoomSheet) {
            BookingRoomSheetStep.None -> Unit
            BookingRoomSheetStep.Corpus -> ModalBottomSheet(
                onDismissRequest = { actionHandler.onAction(BookingFormAction.DismissRoomSelector) }
            ) {
                CorpusSheet(
                    roomGroups = uiState.roomGroups,
                    selectedCorpus = uiState.selectedCorpus,
                    onCorpusClick = { actionHandler.onAction(BookingFormAction.SelectCorpus(it)) }
                )
            }
            BookingRoomSheetStep.Room -> ModalBottomSheet(
                onDismissRequest = { actionHandler.onAction(BookingFormAction.DismissRoomSelector) }
            ) {
                RoomSheet(
                    corpus = uiState.selectedCorpus,
                    query = uiState.roomSearchQuery,
                    rooms = uiState.roomGroups.firstOrNull { it.corpus == uiState.selectedCorpus }?.rooms,
                    selectedRoomId = uiState.selectedRoom?.id,
                    onQueryChange = { actionHandler.onAction(BookingFormAction.ChangeRoomSearchQuery(it)) },
                    onRoomClick = { actionHandler.onAction(BookingFormAction.SelectRoom(it)) }
                )
            }
        }
    }

    if (uiState.showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { actionHandler.onAction(BookingFormAction.DismissDelete) },
            title = { Text("Удалить бронь?") },
            text = { Text("После удаления бронь исчезнет из списка.") },
            confirmButton = {
                TextButton(
                    onClick = { actionHandler.onAction(BookingFormAction.ConfirmDelete) },
                    enabled = !uiState.isDeleting
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { actionHandler.onAction(BookingFormAction.DismissDelete) },
                    enabled = !uiState.isDeleting
                ) {
                    Text("Назад")
                }
            }
        )
    }
}

@Composable
private fun BookingFormContent(
    uiState: BookingFormUiState,
    actionHandler: BookingFormActionHandler,
    modifier: Modifier = Modifier
) {
    var activePicker by rememberSaveable { mutableStateOf<BookingPicker?>(null) }
    val isFormEnabled = !uiState.isSubmitting && !uiState.isDeleting

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 20.dp, bottom = 92.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        RoomSelectorSection(
            selectedCorpus = uiState.selectedCorpus,
            selectedRoom = uiState.selectedRoom,
            enabled = isFormEnabled,
            onCorpusClick = { actionHandler.onAction(BookingFormAction.OpenCorpusSelector) },
            onRoomClick = { actionHandler.onAction(BookingFormAction.OpenRoomSelector) }
        )

        DateTimeSelectionRow(
            dateLabel = "Дата начала",
            dateValue = uiState.selectedDate,
            timeLabel = "Время начала",
            timeValue = uiState.startTime,
            enabled = isFormEnabled,
            onDateClick = { activePicker = BookingPicker.StartDate },
            onTimeClick = { activePicker = BookingPicker.StartTime }
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = uiState.timeMode == BookingTimeMode.EndTime,
                onClick = { actionHandler.onAction(BookingFormAction.ChangeTimeMode(BookingTimeMode.EndTime)) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                enabled = isFormEnabled
            ) { Text("По окончанию") }
            SegmentedButton(
                selected = uiState.timeMode == BookingTimeMode.Duration,
                onClick = { actionHandler.onAction(BookingFormAction.ChangeTimeMode(BookingTimeMode.Duration)) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                enabled = isFormEnabled
            ) { Text("По длительности") }
        }

        if (uiState.timeMode == BookingTimeMode.EndTime) {
            DateTimeSelectionRow(
                dateLabel = "Дата конца",
                dateValue = uiState.endDate,
                timeLabel = "Время конца",
                timeValue = uiState.endTime,
                enabled = isFormEnabled,
                onDateClick = { activePicker = BookingPicker.EndDate },
                onTimeClick = { activePicker = BookingPicker.EndTime }
            )
        } else {
            WiseDubsStableTextField(
                value = uiState.durationMinutes,
                onValueChanged = { actionHandler.onAction(BookingFormAction.ChangeDuration(it)) },
                labelText = "Длительность, минут",
                enabled = isFormEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (uiState.isIntersectionsLoading) {
            BookingIntersectionsLoadingSection(modifier = Modifier.fillMaxWidth())
        } else if (uiState.intersections.isNotEmpty()) {
            BookingIntersectionsSection(
                intersections = uiState.intersections,
                modifier = Modifier.fillMaxWidth()
            )
        }

        WiseDubsStableTextField(
            value = uiState.description,
            onValueChanged = { actionHandler.onAction(BookingFormAction.ChangeDescription(it)) },
            labelText = "Описание",
            enabled = isFormEnabled,
            minLines = 2,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
    }

    when (activePicker) {
        BookingPicker.StartDate -> BookingDatePickerDialog(
            value = uiState.selectedDate,
            minDate = LocalDate.now().minusYears(1),
            onDismiss = { activePicker = null },
            onDateSelected = {
                actionHandler.onAction(BookingFormAction.ChangeDate(it))
                activePicker = null
            }
        )
        BookingPicker.StartTime -> BookingTimePickerDialog(
            title = "Время начала",
            value = uiState.startTime,
            onDismiss = { activePicker = null },
            onTimeSelected = {
                actionHandler.onAction(BookingFormAction.ChangeStartTime(it))
                activePicker = null
            }
        )
        BookingPicker.EndDate -> BookingDatePickerDialog(
            value = uiState.endDate,
            minDate = uiState.selectedDate.toLocalDateOrNull() ?: LocalDate.now(),
            onDismiss = { activePicker = null },
            onDateSelected = {
                actionHandler.onAction(BookingFormAction.ChangeEndDate(it))
                activePicker = null
            }
        )
        BookingPicker.EndTime -> BookingTimePickerDialog(
            title = "Время конца",
            value = uiState.endTime,
            onDismiss = { activePicker = null },
            onTimeSelected = {
                actionHandler.onAction(BookingFormAction.ChangeEndTime(it))
                activePicker = null
            }
        )
        null -> Unit
    }
}

@Composable
private fun BookingIntersectionsLoadingSection(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Проверяем пересечения",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
private fun BookingIntersectionsSection(
    intersections: ImmutableList<BookingUiModel>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.22f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Выберите другое время, найдено ${intersections.size} ${intersections.size.intersectionsWord()}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                intersections.forEach { booking ->
                    BookingIntersectionItem(booking = booking)
                }
            }
        }
    }
}

@Composable
private fun BookingIntersectionItem(
    booking: BookingUiModel,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = booking.intervalText,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                BookingIntersectionBadge(isMine = booking.isMine)
            }
            Text(
                text = booking.roomName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = booking.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Telegram: ${booking.authorTelegramId.asTelegramHandle()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BookingIntersectionBadge(
    isMine: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = if (isMine) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Text(
            text = if (isMine) "Моя бронь" else "Другая бронь",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (isMine) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = FontWeight.SemiBold
        )
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
            value = dateValue,
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

private enum class BookingPicker {
    StartDate,
    StartTime,
    EndDate,
    EndTime
}

private fun Int.intersectionsWord(): String {
    val lastTwoDigits = this % 100
    if (lastTwoDigits in 11..14) return "пересечений"
    return when (this % 10) {
        1 -> "пересечение"
        2, 3, 4 -> "пересечения"
        else -> "пересечений"
    }
}

private fun String.asTelegramHandle(): String =
    trim().let { value ->
        when {
            value.isBlank() -> "не указан"
            value.startsWith("@") -> value
            else -> "@$value"
        }
    }
