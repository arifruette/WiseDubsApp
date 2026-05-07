package ru.ari.booking.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import ru.ari.booking.presentation.contract.BookingActionHandler
import ru.ari.booking.presentation.contract.BookingScreenAction
import ru.ari.booking.presentation.contract.BookingScreenUiState
import ru.ari.booking.presentation.models.BookingRoomSheetStep
import ru.ari.booking.presentation.models.BookingRoomsLoadState
import ru.ari.designsystem.components.WiseDubsProgressIndicator
import ru.ari.designsystem.components.WiseDubsTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    uiState: BookingScreenUiState,
    actionHandler: BookingActionHandler,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { WiseDubsTopAppBar(title = "Занятость комнат") },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (uiState.roomsLoadState == BookingRoomsLoadState.Content) {
                FloatingActionButton(
                    onClick = { actionHandler.onAction(BookingScreenAction.ClickCreate) },
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .offset(y = 16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Создать бронь")
                }
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRoomsRefreshing || uiState.isBookingsRefreshing,
            onRefresh = { actionHandler.onAction(BookingScreenAction.Refresh) },
            state = rememberPullToRefreshState(),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.roomsLoadState) {
                BookingRoomsLoadState.Loading -> WiseDubsProgressIndicator()
                BookingRoomsLoadState.Empty -> EmptyState(title = "Комнат пока нет")
                is BookingRoomsLoadState.Error -> EmptyState(title = "Не удалось загрузить комнаты")
                BookingRoomsLoadState.Content -> BookingContent(
                    uiState = uiState,
                    actionHandler = actionHandler
                )
            }
        }

        when (uiState.activeRoomSheet) {
            BookingRoomSheetStep.None -> Unit
            BookingRoomSheetStep.Corpus -> ModalBottomSheet(
                onDismissRequest = { actionHandler.onAction(BookingScreenAction.DismissRoomSelector) }
            ) {
                CorpusSheet(
                    roomGroups = uiState.roomGroups,
                    selectedCorpus = uiState.selectedCorpus,
                    onCorpusClick = { actionHandler.onAction(BookingScreenAction.SelectCorpus(it)) }
                )
            }
            BookingRoomSheetStep.Room -> ModalBottomSheet(
                onDismissRequest = { actionHandler.onAction(BookingScreenAction.DismissRoomSelector) }
            ) {
                RoomSheet(
                    corpus = uiState.selectedCorpus,
                    query = uiState.roomSearchQuery,
                    rooms = uiState.roomGroups.firstOrNull { it.corpus == uiState.selectedCorpus }?.rooms,
                    selectedRoomId = uiState.selectedRoom?.id,
                    onQueryChange = { actionHandler.onAction(BookingScreenAction.ChangeRoomSearchQuery(it)) },
                    onRoomClick = { actionHandler.onAction(BookingScreenAction.SelectRoom(it)) }
                )
            }
        }
    }
}

@Composable
private fun BookingContent(
    uiState: BookingScreenUiState,
    actionHandler: BookingActionHandler,
    modifier: Modifier = Modifier
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        RoomSelectorSection(
            selectedCorpus = uiState.selectedCorpus,
            selectedRoom = uiState.selectedRoom,
            enabled = true,
            onCorpusClick = { actionHandler.onAction(BookingScreenAction.OpenCorpusSelector) },
            onRoomClick = { actionHandler.onAction(BookingScreenAction.OpenRoomSelector) }
        )

        BookingSelectionField(
            label = "Дата",
            value = uiState.selectedDate.toDisplayBookingDate(),
            placeholder = "Выбрать дату",
            enabled = true,
            onClick = { showDatePicker = true },
            fieldHeight = 74.dp
        )

        BookingsSection(
            loadState = uiState.bookingsLoadState,
            bookings = uiState.bookings,
            hasSelectedRoom = uiState.selectedRoom != null,
            onBookingClick = { actionHandler.onAction(BookingScreenAction.ClickBooking(it)) }
        )
    }

    if (showDatePicker) {
        BookingDatePickerDialog(
            value = uiState.selectedDate,
            minDate = LocalDate.now().minusYears(1),
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                actionHandler.onAction(BookingScreenAction.ChangeDate(it))
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun EmptyState(
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun String.toDisplayBookingDate(): String =
    try {
        LocalDate.parse(this).format(DISPLAY_DATE_FORMATTER)
    } catch (_: DateTimeParseException) {
        this
    }

private val DISPLAY_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
