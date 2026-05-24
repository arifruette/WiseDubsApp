package ru.ari.booking.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.ari.booking.domain.models.MyBookingsPeriod
import ru.ari.booking.presentation.contract.MyBookingsAction
import ru.ari.booking.presentation.contract.MyBookingsUiState
import ru.ari.booking.presentation.models.BookingPostsLoadState
import ru.ari.booking.presentation.models.BookingUiModel
import ru.ari.designsystem.components.WiseDubsProgressIndicator
import ru.ari.designsystem.components.WiseDubsTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    uiState: MyBookingsUiState,
    onAction: (MyBookingsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            WiseDubsTopAppBar(
                title = "Мои брони",
                onBackClick = { onAction(MyBookingsAction.ClickBack) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PrimaryTabRow(selectedTabIndex = uiState.selectedPeriod.ordinal) {
                MyBookingsPeriod.entries.forEach { period ->
                    Tab(
                        selected = uiState.selectedPeriod == period,
                        onClick = { onAction(MyBookingsAction.SelectPeriod(period)) },
                        text = {
                            Text(
                                text = period.title(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { onAction(MyBookingsAction.Refresh) },
                state = rememberPullToRefreshState(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    when (val loadState = uiState.loadState) {
                        BookingPostsLoadState.Loading -> WiseDubsProgressIndicator()
                        BookingPostsLoadState.Idle -> Unit
                        BookingPostsLoadState.Empty -> EmptyBookings(text = "Броней пока нет")
                        is BookingPostsLoadState.Error -> ErrorBookings(
                            message = loadState.message,
                            onRetry = { onAction(MyBookingsAction.Refresh) }
                        )
                        BookingPostsLoadState.Content -> LazyColumn(
                            contentPadding = PaddingValues(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.bookings, key = { it.id }) { booking ->
                                MyBookingCard(
                                    booking = booking,
                                    onClick = { onAction(MyBookingsAction.ClickEdit(booking.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun MyBookingCard(
    booking: BookingUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(booking.roomName, fontWeight = FontWeight.SemiBold)
            Text(booking.intervalText, style = MaterialTheme.typography.bodyMedium)
            Text(
                booking.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyBookings(text: String) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = text, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun ErrorBookings(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
            Text("Повторить")
        }
    }
}

private fun MyBookingsPeriod.title(): String = when (this) {
    MyBookingsPeriod.Upcoming -> "Будущие"
    MyBookingsPeriod.Past -> "Прошедшие"
}
