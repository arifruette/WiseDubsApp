package ru.ari.managepost.presentation.ui

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.DoorFront
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import ru.ari.designsystem.components.WiseDubsTextField
import ru.ari.managepost.presentation.contract.ManagePostActionHandler
import ru.ari.managepost.presentation.contract.ManagePostScreenAction
import ru.ari.managepost.presentation.contract.ManagePostScreenUiState
import ru.ari.managepost.presentation.models.ManagePostImageUiModel
import ru.ari.managepost.presentation.models.ManagePostMode
import ru.ari.managepost.presentation.models.ManagePostRoomsLoadState
import ru.ari.managepost.presentation.models.ManagePostSelectorSheet
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePostScreen(
    uiState: ManagePostScreenUiState,
    actionHandler: ManagePostActionHandler,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.toString()?.let { actionHandler.onAction(ManagePostScreenAction.AddImage(it)) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (uiState.mode) {
                            ManagePostMode.Create -> "Добавить объявление"
                            is ManagePostMode.Edit -> "Редактирование"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(bottom = 24.dp)
        ) {
            ImageSection(
                images = uiState.form.images,
                onAddClick = {
                    imagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemoveClick = { key ->
                    actionHandler.onAction(ManagePostScreenAction.RemoveImage(key))
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WiseDubsTextField(
                    value = uiState.form.title,
                    onValueChanged = { actionHandler.onAction(ManagePostScreenAction.ChangeTitle(it)) },
                    labelText = "Заголовок",
                    modifier = Modifier.fillMaxWidth()
                )

                WiseDubsTextField(
                    value = uiState.form.description,
                    onValueChanged = {
                        actionHandler.onAction(ManagePostScreenAction.ChangeDescription(it))
                    },
                    labelText = "Описание",
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                WiseDubsTextField(
                    value = uiState.form.exchange,
                    onValueChanged = { actionHandler.onAction(ManagePostScreenAction.ChangeExchange(it)) },
                    labelText = "Обмен",
                    modifier = Modifier.fillMaxWidth()
                )

                RoomSelectorSection(
                    uiState = uiState,
                    onCorpusClick = {
                        actionHandler.onAction(ManagePostScreenAction.OpenCorpusSelector)
                    },
                    onRoomClick = {
                        actionHandler.onAction(ManagePostScreenAction.OpenRoomSelector)
                    },
                    onRetryClick = {
                        actionHandler.onAction(ManagePostScreenAction.RetryLoadRooms)
                    }
                )

                if (uiState.mode is ManagePostMode.Edit && uiState.form.reservedBy.isNotBlank()) {
                    WiseDubsTextField(
                        value = uiState.form.reservedBy,
                        onValueChanged = {},
                        labelText = "Зарезервировано",
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val localFilesByKey = uiState.form.images
                        .filterIsInstance<ManagePostImageUiModel.Local>()
                        .mapNotNull { image ->
                            image.previewUrl.toFileOrNull(context)?.let { file -> image.key to file }
                        }
                        .toMap()

                    actionHandler.onAction(
                        ManagePostScreenAction.Submit(
                            images = uiState.form.images.toList(),
                            localFilesByKey = localFilesByKey
                        )
                    )
                },
                enabled = !uiState.isSaving,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = when (uiState.mode) {
                            ManagePostMode.Create -> "Создать пост"
                            is ManagePostMode.Edit -> "Сохранить изменения"
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        when (uiState.activeSelectorSheet) {
            ManagePostSelectorSheet.None -> Unit
            ManagePostSelectorSheet.Corpus -> {
                ModalBottomSheet(
                    onDismissRequest = {
                        actionHandler.onAction(ManagePostScreenAction.DismissSelector)
                    }
                ) {
                    SelectorSheetContent(
                        title = "Выберите корпус",
                        subtitle = "Сначала выберите корпус, затем комнату.",
                        items = uiState.availableCorpora,
                        selectedValue = uiState.form.selectedCorpus,
                        onSelect = {
                            actionHandler.onAction(ManagePostScreenAction.SelectCorpus(it))
                        }
                    )
                }
            }

            ManagePostSelectorSheet.Room -> {
                ModalBottomSheet(
                    onDismissRequest = {
                        actionHandler.onAction(ManagePostScreenAction.DismissSelector)
                    }
                ) {
                    RoomSheetContent(
                        corpus = uiState.form.selectedCorpus,
                        query = uiState.roomSearchQuery,
                        rooms = uiState.filteredAvailableRooms,
                        selectedRoom = uiState.form.selectedRoom,
                        onQueryChange = {
                            actionHandler.onAction(ManagePostScreenAction.ChangeRoomSearchQuery(it))
                        },
                        onSelect = {
                            actionHandler.onAction(ManagePostScreenAction.SelectRoom(it))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoomSelectorSection(
    uiState: ManagePostScreenUiState,
    onCorpusClick: () -> Unit,
    onRoomClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Где забрать",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        when (val roomsState = uiState.roomsLoadState) {
            ManagePostRoomsLoadState.Loading -> {
                SelectorStatusCard(
                    title = "Загружаем корпуса и комнаты",
                    subtitle = "Список нужен для точного выбора места.",
                    action = {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                )
            }

            is ManagePostRoomsLoadState.Error -> {
                SelectorStatusCard(
                    title = "Не удалось загрузить список",
                    subtitle = roomsState.message,
                    action = {
                        TextButton(onClick = onRetryClick) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = null
                            )
                            Text("Повторить")
                        }
                    }
                )
            }

            ManagePostRoomsLoadState.Content -> {
                SelectionField(
                    label = "Корпус",
                    value = uiState.form.selectedCorpus,
                    placeholder = "Выбрать корпус",
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Apartment,
                            contentDescription = null
                        )
                    },
                    onClick = onCorpusClick
                )

                SelectionField(
                    label = "Комната",
                    value = uiState.form.selectedRoom,
                    placeholder = if (uiState.form.selectedCorpus.isBlank()) {
                        "Сначала выберите корпус"
                    } else {
                        "Выбрать комнату"
                    },
                    enabled = uiState.form.selectedCorpus.isNotBlank(),
                    supportingText = uiState.form.selectedCorpus.takeIf { it.isNotBlank() }?.let {
                        "Корпус $it"
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.DoorFront,
                            contentDescription = null
                        )
                    },
                    onClick = onRoomClick
                )
            }
        }
    }
}

@Composable
private fun SelectionField(
    label: String,
    value: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val borderColor = if (enabled) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)
    }
    val backgroundColor = if (enabled) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(20.dp)),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(12.dp)
            ) {
                icon()
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value.ifBlank { placeholder },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isBlank()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    fontWeight = if (value.isBlank()) FontWeight.Normal else FontWeight.Medium
                )
                supportingText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun SelectorStatusCard(
    title: String,
    subtitle: String,
    action: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            action()
        }
    }
}

@Composable
private fun SelectorSheetContent(
    title: String,
    subtitle: String,
    items: ImmutableList<String>,
    selectedValue: String,
    onSelect: (String) -> Unit,
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
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it }) { item ->
                SelectorListItem(
                    label = item,
                    selected = item == selectedValue,
                    onClick = { onSelect(item) }
                )
            }
        }
    }
}

@Composable
private fun RoomSheetContent(
    corpus: String,
    query: String,
    rooms: ImmutableList<String>,
    selectedRoom: String,
    onQueryChange: (String) -> Unit,
    onSelect: (String) -> Unit,
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

        WiseDubsTextField(
            value = query,
            onValueChanged = onQueryChange,
            labelText = "Поиск по комнате",
            modifier = Modifier.fillMaxWidth()
        )

        if (rooms.isEmpty()) {
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
                items(rooms, key = { it }) { room ->
                    SelectorListItem(
                        label = room,
                        selected = room == selectedRoom,
                        onClick = { onSelect(room) }
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

@Composable
private fun ImageSection(
    images: ImmutableList<ManagePostImageUiModel>,
    onAddClick: () -> Unit,
    onRemoveClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 30.dp)
    ) {
        items(items = images, key = ManagePostImageUiModel::key) { image ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = image.previewUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(140.dp)
                        .aspectRatio(1f)
                )
                IconButton(
                    onClick = { onRemoveClick(image.key) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Удалить"
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .height(140.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable(onClick = onAddClick),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Добавить фото",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun String.toFileOrNull(context: Context): File? {
    val uri = runCatching { this.toUri() }.getOrNull() ?: return null
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val tempFile = File.createTempFile("manage_post_local_", ".jpg", context.cacheDir)

    inputStream.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return tempFile
}
