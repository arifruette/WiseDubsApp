package ru.ari.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
                .padding(horizontal = 22.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                ProfileCard(
                    isLoading = uiState.isProfileLoading,
                    profile = uiState.profile,
                    error = uiState.profileError,
                    onRetry = { onAction(ProfileScreenAction.RetryProfile) }
                )

                ProfileSections(
                    onReservedPostsClick = { onAction(ProfileScreenAction.ClickReservedPosts) },
                    onMyBookingsClick = { onAction(ProfileScreenAction.ClickMyBookings) }
                )
            }

            OutlinedButton(
                onClick = { onAction(ProfileScreenAction.ClickLogout) },
                shape = RoundedCornerShape(16.dp),
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
private fun ProfileCard(
    isLoading: Boolean,
    profile: ProfileUiModel?,
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        when {
            isLoading -> WiseDubsProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(196.dp)
            )

            error != null -> ErrorBlock(
                message = error,
                onRetry = onRetry,
                modifier = Modifier.padding(18.dp)
            )

            profile != null -> ProfileCardContent(
                profile = profile,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )

            else -> ErrorBlock(
                message = "Не удалось загрузить профиль",
                onRetry = onRetry,
                modifier = Modifier.padding(18.dp)
            )
        }
    }
}

@Composable
private fun ProfileCardContent(
    profile: ProfileUiModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            InitialsAvatar(initials = profile.avatarInitials())

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = profile.displayName(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Студент",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 14.dp, bottom = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
        )

        ProfileInfoRow(label = "Email", value = profile.email)
    }
}

@Composable
private fun ProfileSections(
    onReservedPostsClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Мои разделы",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileSectionRow(
                    text = "Забронированные посты",
                    onClick = onReservedPostsClick
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                )

                ProfileSectionRow(
                    text = "Забронированные досуговые",
                    onClick = onMyBookingsClick
                )
            }
        }
    }
}

@Composable
private fun ProfileSectionRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
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
            .size(56.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            )
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
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
            Icon(
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

private fun ProfileUiModel.displayName(): String =
    telegramId.trim()
        .ifBlank { email.trim() }
        .ifBlank { "Пользователь" }

private fun ProfileUiModel.avatarInitials(): String =
    displayName()
        .take(2)
        .uppercase()
