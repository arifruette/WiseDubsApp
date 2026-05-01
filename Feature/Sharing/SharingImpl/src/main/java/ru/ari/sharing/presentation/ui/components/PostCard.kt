package ru.ari.sharing.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.ari.sharing.presentation.models.PostReservationStatusUi
import ru.ari.sharing.presentation.models.PostUiModel

@Composable
fun PostCard(
    post: PostUiModel,
    onBookingButtonClick: (Long) -> Unit,
    navigateToDetailsScreen: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val highlightedBackground = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)

    Box(
        modifier = modifier
            .padding(12.dp)
            .clip(shape)
            .background(color = highlightedBackground)
            .clickable { navigateToDetailsScreen(post.id) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            if (post.images.isNotEmpty()) {
                ImagesCarousel(post.images)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = post.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            ReservationStatusBadge(status = post.reservationStatus)
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Обмен: ${post.exchange}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Контакт: ${post.authorEmail ?: "не указан"}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(12.dp))

            Button(
                shape = RoundedCornerShape(12.dp),
                elevation = null,
                onClick = {
                    when (post.reservationStatus) {
                        PostReservationStatusUi.AVAILABLE -> onBookingButtonClick(post.id)
                        PostReservationStatusUi.RESERVED_BY_ME -> navigateToDetailsScreen(post.id)
                        PostReservationStatusUi.RESERVED_BY_OTHER -> Unit
                    }
                },
                enabled = post.reservationStatus != PostReservationStatusUi.RESERVED_BY_OTHER,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (post.reservationStatus) {
                        PostReservationStatusUi.AVAILABLE -> "Забронировать"
                        PostReservationStatusUi.RESERVED_BY_ME -> "Подробнее"
                        PostReservationStatusUi.RESERVED_BY_OTHER -> "Занято"
                    },
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
private fun ReservationStatusBadge(
    status: PostReservationStatusUi,
    modifier: Modifier = Modifier
) {
    val (label, containerColor, contentColor) = when (status) {
        PostReservationStatusUi.AVAILABLE -> Triple(
            "Свободно",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        PostReservationStatusUi.RESERVED_BY_ME -> Triple(
            "Забронировано вами",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        PostReservationStatusUi.RESERVED_BY_OTHER -> Triple(
            "Занято",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    }

    Row(modifier = modifier.fillMaxWidth()) {
        Surface(
            color = containerColor,
            contentColor = contentColor,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
        Spacer(modifier = Modifier.width(1.dp))
    }
}
