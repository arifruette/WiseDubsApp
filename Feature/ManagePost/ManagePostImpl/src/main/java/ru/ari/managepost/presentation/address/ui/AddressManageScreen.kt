package ru.ari.managepost.presentation.address.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.ari.designsystem.components.WiseDubsStableTextField
import ru.ari.designsystem.components.WiseDubsTopAppBar
import ru.ari.managepost.presentation.address.contract.AddressManageAction
import ru.ari.managepost.presentation.address.contract.AddressManageActionHandler
import ru.ari.managepost.presentation.address.contract.AddressManageState

@Composable
fun AddressManageScreen(
    state: AddressManageState,
    actionHandler: AddressManageActionHandler,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    locationId: Int? = null
) {
    if (state.isLoading) {
        LoadingScreen(modifier = modifier)
        return
    }

    val isEditMode = locationId != null
    val isFormEnabled = !state.isSaving

    Scaffold(
        modifier = modifier,
        topBar = {
            WiseDubsTopAppBar(
                title = if (isEditMode) "Редактировать адрес" else "Добавить адрес",
                onBackClick = onBackClick,
                actions = {
                    if (isEditMode) {
                        IconButton(
                            onClick = { actionHandler.onAction(AddressManageAction.Delete) },
                            enabled = isFormEnabled
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 30.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WiseDubsStableTextField(
                value = state.corpus,
                onValueChanged = { actionHandler.onAction(AddressManageAction.ChangeCorpus(it)) },
                labelText = "Дом / Корпус",
                enabled = isFormEnabled,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WiseDubsStableTextField(
                    value = state.entrance,
                    onValueChanged = { actionHandler.onAction(AddressManageAction.ChangeEntrance(it)) },
                    labelText = "Подъезд",
                    enabled = isFormEnabled,
                    modifier = Modifier.weight(1f)
                )

                WiseDubsStableTextField(
                    value = state.floor,
                    onValueChanged = { actionHandler.onAction(AddressManageAction.ChangeFloor(it)) },
                    labelText = "Этаж",
                    enabled = isFormEnabled,
                    modifier = Modifier.weight(1f)
                )
            }

            WiseDubsStableTextField(
                value = state.room,
                onValueChanged = { actionHandler.onAction(AddressManageAction.ChangeRoom(it)) },
                labelText = "Квартира / Офис",
                enabled = isFormEnabled,
                isError = state.isRoomError,
                supportingText = if (state.isRoomError) {
                    { Text(text = "Это поле обязательно для заполнения") }
                } else {
                    null
                },
                modifier = Modifier.fillMaxWidth()
            )

            WiseDubsStableTextField(
                value = state.label,
                onValueChanged = { actionHandler.onAction(AddressManageAction.ChangeLabel(it)) },
                labelText = "Название",
                enabled = isFormEnabled,
                modifier = Modifier.fillMaxWidth()
            )

            WiseDubsStableTextField(
                value = state.comment,
                onValueChanged = { actionHandler.onAction(AddressManageAction.ChangeComment(it)) },
                labelText = "Комментарий (необязательно)",
                enabled = isFormEnabled,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { actionHandler.onAction(AddressManageAction.Save) },
                enabled = !state.isSaving,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = if (isEditMode) "Обновить" else "Сохранить",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
