package ru.ari.sharing.presentation.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class PostUiModel(
    val id: Long,
    val title: String,
    val exchange: String,
    val authorEmail: String?,
    val images: ImmutableList<PostImageUiModel>
)

@Immutable
data class PostImageUiModel(
    val id: Long,
    val url: String
)
