package ru.ari.myposts.presentation.contract

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import ru.ari.myposts.presentation.models.MyPostUiModel

@Immutable
sealed interface MyPostsScreenUiState {

    val selectedTab: MyPostsTab

    @Immutable
    data class Loading(
        override val selectedTab: MyPostsTab = MyPostsTab.Active
    ) : MyPostsScreenUiState

    @Immutable
    data class Empty(
        override val selectedTab: MyPostsTab
    ) : MyPostsScreenUiState

    @Immutable
    data class Content(
        override val selectedTab: MyPostsTab,
        val posts: ImmutableList<MyPostUiModel>,
        val isRefreshing: Boolean
    ) : MyPostsScreenUiState
}
