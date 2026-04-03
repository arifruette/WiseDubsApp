package ru.ari.myposts.presentation.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class MyPostUiModel(
    val id: Long,
    val title: String,
    val exchange: String,
    val isActive: Boolean,
    val imageUrls: ImmutableList<String>
)
