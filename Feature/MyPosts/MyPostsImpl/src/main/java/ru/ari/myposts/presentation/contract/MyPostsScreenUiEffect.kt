package ru.ari.myposts.presentation.contract

import androidx.compose.runtime.Immutable

@Immutable
sealed interface MyPostsScreenUiEffect {

    @Immutable
    data class ShowError(val message: String) : MyPostsScreenUiEffect

    @Immutable
    data object OpenCreatePost : MyPostsScreenUiEffect

    @Immutable
    data class OpenEditPost(val postId: Long) : MyPostsScreenUiEffect
}
