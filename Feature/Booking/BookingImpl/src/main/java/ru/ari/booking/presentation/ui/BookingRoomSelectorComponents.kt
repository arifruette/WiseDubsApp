package ru.ari.booking.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.ari.booking.presentation.models.BookingRoomGroupUiModel
import ru.ari.booking.presentation.models.BookingRoomUiModel
import ru.ari.designsystem.components.WiseDubsStableTextField

@Composable
fun RoomSelectorSection(
    selectedCorpus: String,
    selectedRoom: BookingRoomUiModel?,
    enabled: Boolean,
    onCorpusClick: () -> Unit,
    onRoomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BookingSelectionField(
            label = "Корпус",
            value = selectedCorpus,
            placeholder = "Выбрать корпус",
            enabled = enabled,
            onClick = onCorpusClick
        )

        BookingSelectionField(
            label = "Комната",
            value = selectedRoom?.name.orEmpty(),
            placeholder = if (selectedCorpus.isBlank()) {
                "Сначала выберите корпус"
            } else {
                "Выбрать комнату"
            },
            enabled = enabled && selectedCorpus.isNotBlank(),
            supportingText = selectedCorpus.takeIf { it.isNotBlank() }?.let { "Корпус $it" },
            onClick = onRoomClick
        )
    }
}

@Composable
fun BookingSelectionField(
    label: String,
    value: String,
    placeholder: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    fieldHeight: Dp? = null,
    valueMaxLines: Int = 2
) {
    val backgroundColor = if (enabled) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.24f)
    }
    val fieldModifier = if (fieldHeight == null) {
        modifier.fillMaxWidth()
    } else {
        modifier
            .fillMaxWidth()
            .height(fieldHeight)
    }

    Surface(
        modifier = fieldModifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = enabled, onClick = onClick),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value.ifBlank { placeholder },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isBlank()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    fontWeight = if (value.isBlank()) FontWeight.Normal else FontWeight.Medium,
                    maxLines = valueMaxLines,
                    overflow = TextOverflow.Ellipsis
                )
                supportingText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                tint = if (enabled) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
        }
    }
}

@Composable
fun CorpusSheet(
    roomGroups: ImmutableList<BookingRoomGroupUiModel>,
    selectedCorpus: String,
    onCorpusClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Выберите корпус",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Сначала выберите корпус, затем комнату.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(roomGroups, key = { it.corpus }) { group ->
                SelectorListItem(
                    label = group.corpus,
                    selected = group.corpus == selectedCorpus,
                    onClick = { onCorpusClick(group.corpus) }
                )
            }
        }
    }
}

@Composable
fun RoomSheet(
    corpus: String,
    query: String,
    rooms: ImmutableList<BookingRoomUiModel>?,
    selectedRoomId: Int?,
    onQueryChange: (String) -> Unit,
    onRoomClick: (BookingRoomUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredRooms = remember(rooms, query) {
        rooms.orEmpty().filter { room ->
            query.isBlank() || room.name.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Выберите комнату",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Корпус $corpus",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        WiseDubsStableTextField(
            value = query,
            onValueChanged = onQueryChange,
            labelText = "Поиск по комнате",
            modifier = Modifier.fillMaxWidth()
        )

        if (filteredRooms.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Text(
                    text = "По вашему запросу ничего не найдено.",
                    modifier = Modifier.padding(18.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredRooms, key = { it.id }) { room ->
                    SelectorListItem(
                        label = room.name,
                        selected = room.id == selectedRoomId,
                        onClick = { onRoomClick(room) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectorListItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        color = background
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
