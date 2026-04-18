package ru.ari.myposts.presentation.models

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.Immutable

@Immutable
data class MyPostUiModel(
    val id: Long,
    val title: String,
    val exchangeText: String,
    val isActive: Boolean,
    val previewImageUrl: String?,
    val archiveIcon: ImageVector,
    val archiveActionTargetActive: Boolean,
    val archiveActionEnabled: Boolean = true
)
