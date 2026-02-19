package ru.ari.sharing.presentation.contract

import androidx.compose.runtime.Immutable
import ru.ari.sharing.api.domain.models.Post

@Immutable
data class SharingScreenUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val posts: List<Post> = emptyList()
)
