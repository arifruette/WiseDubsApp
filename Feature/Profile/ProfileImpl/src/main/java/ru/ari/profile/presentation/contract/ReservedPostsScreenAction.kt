package ru.ari.profile.presentation.contract

sealed interface ReservedPostsScreenAction {
    data object Load : ReservedPostsScreenAction
    data object Retry : ReservedPostsScreenAction
    data object ClickBack : ReservedPostsScreenAction
    data object ClickFindPosts : ReservedPostsScreenAction
    data class ClickReservedPost(val postId: Long) : ReservedPostsScreenAction
}
