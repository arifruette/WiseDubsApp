package ru.ari.managepost.presentation.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ManagePostFormUiModel(
    val title: String = "",
    val description: String = "",
    val exchange: String = "",
    val selectedCorpus: String = "",
    val selectedRoom: String = "",
    val reservedBy: String = "",
    val messageId: String = "",
    val images: ImmutableList<ManagePostImageUiModel> = persistentListOf(),
    val imagesChanged: Boolean = false
)
