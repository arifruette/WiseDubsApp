package ru.ari.booking.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.ari.booking.presentation.models.BookingPostsLoadState
import ru.ari.booking.presentation.models.BookingUiModel

@Composable
fun BookingsSection(
    loadState: BookingPostsLoadState,
    bookings: ImmutableList<BookingUiModel>,
    hasSelectedRoom: Boolean,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (loadState) {
            BookingPostsLoadState.Idle -> StatusSurface(
                title = if (hasSelectedRoom) "Заполните интервал" else "Выберите комнату",
                subtitle = "После выбора комнаты и времени здесь появятся брони."
            )

            BookingPostsLoadState.Loading -> StatusSurface(
                title = "Загружаем занятость",
                subtitle = "Проверяем выбранный интервал.",
                trailing = {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            )

            BookingPostsLoadState.Empty -> StatusSurface(
                title = "Свободно",
                subtitle = "Для выбранного интервала брони не найдены."
            )

            is BookingPostsLoadState.Error -> StatusSurface(
                title = "Не удалось загрузить занятость",
                subtitle = loadState.message,
                trailing = {
                    TextButton(onClick = onRetryClick) {
                        Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
                        Text("Повторить")
                    }
                }
            )

            BookingPostsLoadState.Content -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                bookings.forEach { booking ->
                    BookingItem(booking = booking)
                }
            }
        }
    }
}

@Composable
private fun BookingItem(
    booking: BookingUiModel,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = booking.intervalText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = booking.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusSurface(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = title, fontWeight = FontWeight.SemiBold)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            trailing?.invoke()
        }
    }
}
