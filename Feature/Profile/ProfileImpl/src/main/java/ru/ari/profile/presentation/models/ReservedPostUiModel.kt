package ru.ari.profile.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class ReservedPostUiModel(
    val id: Long,
    val title: String,
    val description: String,
    val exchange: String,
    val pickupLocation: String
)
