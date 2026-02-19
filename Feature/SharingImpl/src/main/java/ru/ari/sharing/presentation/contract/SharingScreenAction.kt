package ru.ari.sharing.presentation.contract

import androidx.compose.runtime.Stable

@Stable
sealed interface SharingScreenAction {
    data object LoadPosts : SharingScreenAction
    data object RefreshPosts : SharingScreenAction
    data object RetryLoadPosts : SharingScreenAction
    data class OpenPostDetails(val postId: Long) : SharingScreenAction
    data class BookItem(val postId: Long) : SharingScreenAction
}
