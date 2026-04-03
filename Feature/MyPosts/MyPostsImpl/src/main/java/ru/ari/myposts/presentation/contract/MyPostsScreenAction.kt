package ru.ari.myposts.presentation.contract

import androidx.compose.runtime.Immutable

@Immutable
sealed interface MyPostsScreenAction {

    @Immutable
    data object Load : MyPostsScreenAction

    @Immutable
    data object Refresh : MyPostsScreenAction

    @Immutable
    data object ClickCreate : MyPostsScreenAction

    @Immutable
    data class SelectTab(val tab: MyPostsTab) : MyPostsScreenAction

    @Immutable
    data class ClickPost(val postId: Long) : MyPostsScreenAction
}
