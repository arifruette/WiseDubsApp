package ru.ari.managepost.presentation.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class ManagePostRoomGroupUiModel(
    val corpus: String,
    val rooms: ImmutableList<String>
)
