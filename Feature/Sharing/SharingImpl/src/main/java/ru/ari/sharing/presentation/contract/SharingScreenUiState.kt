package ru.ari.sharing.presentation.contract

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import ru.ari.sharing.presentation.models.PostUiModel

@Immutable
sealed interface SharingScreenUiState {

    @Immutable
    data object Loading : SharingScreenUiState

    @Immutable
    data class Empty(
        val isRefreshing: Boolean = false
    ) : SharingScreenUiState

    @Immutable
    data class Content(
        val posts: ImmutableList<PostUiModel>,
        val isRefreshing: Boolean
    ) : SharingScreenUiState
}
