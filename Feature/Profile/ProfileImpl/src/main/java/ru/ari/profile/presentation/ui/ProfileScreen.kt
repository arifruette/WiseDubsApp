package ru.ari.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.ari.designsystem.components.WiseDubsProgressIndicator
import ru.ari.designsystem.components.WiseDubsTopAppBar
import ru.ari.profile.presentation.contract.ProfileScreenAction
import ru.ari.profile.presentation.contract.ProfileScreenUiState
import ru.ari.profile.presentation.models.ProfileUiModel

@Composable
fun ProfileScreen(
    uiState: ProfileScreenUiState,
    onAction: (ProfileScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isLogoutLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize()
        ) {
            WiseDubsProgressIndicator()
        }
        return
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            WiseDubsTopAppBar(title = "Профиль")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val initials = uiState.profile?.avatarInitials().orEmpty()
                InitialsAvatar(initials = initials.ifBlank { "?" })
                ProfileSection(
                    isLoading = uiState.isProfileLoading,
                    profile = uiState.profile,
                    error = uiState.profileError,
                    onRetry = { onAction(ProfileScreenAction.RetryProfile) }
                )
                Button(
                    onClick = { onAction(ProfileScreenAction.ClickReservedPosts) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Забронированные посты",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    onClick = { onAction(ProfileScreenAction.ClickMyBookings) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Мои брони",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            OutlinedButton(
                onClick = { onAction(ProfileScreenAction.ClickLogout) },
                shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text(
                    text = "Выйти",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (uiState.showLogoutDialog) {
        LogoutDialog(
            onConfirm = { onAction(ProfileScreenAction.ConfirmLogout) },
            onDismiss = { onAction(ProfileScreenAction.DismissLogout) }
        )
    }
}

@Composable
private fun ProfileSection(
    isLoading: Boolean,
    profile: ProfileUiModel?,
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        when {
            isLoading -> WiseDubsProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
            )
            error != null -> ErrorBlock(
                message = error,
                onRetry = onRetry,
                modifier = Modifier.padding(16.dp)
            )
            profile != null -> Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileInfoRow(label = "Email", value = profile.email)
                ProfileInfoRow(label = "Telegram", value = profile.telegramId)
                if (profile.email.isBlank() && profile.telegramId.isBlank()) {
                    Text(
                        text = "Нет данных о профиле",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> ErrorBlock(
                message = "Не удалось загрузить профиль",
                onRetry = onRetry,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.ifBlank { "Не указан" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
    }
}

@Composable
private fun InitialsAvatar(
    initials: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(80.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun ErrorBlock(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Button(onClick = onRetry) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null
            )
            Text(
                text = "Повторить",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Выйти из аккаунта?")
        },
        text = {
            Text(text = "После выхода потребуется снова войти в приложение.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Выйти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Отмена")
            }
        }
    )
}

private fun ProfileUiModel.avatarInitials(): String {
    val source = telegramId.ifBlank { email }
    return source
        .trim()
        .take(2)
        .uppercase()
}
