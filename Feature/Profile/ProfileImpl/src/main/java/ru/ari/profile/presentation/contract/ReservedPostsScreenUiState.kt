package ru.ari.profile.presentation.contract

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.ari.profile.presentation.models.ReservedPostUiModel

@Immutable
data class ReservedPostsScreenUiState(
    val isLoading: Boolean = true,
    val reservedPosts: ImmutableList<ReservedPostUiModel> = persistentListOf(),
    val error: String? = null
)
