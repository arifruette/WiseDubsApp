package ru.ari.profile.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileUiModel(
    val email: String,
    val telegramId: String
)
