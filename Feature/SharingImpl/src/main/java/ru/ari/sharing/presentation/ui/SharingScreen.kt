@file:Suppress("DEPRECATION")

package ru.ari.sharing.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.ari.designsystem.components.WiseDubsProgressIndicator
import ru.ari.sharing.presentation.constants.DefaultPost
import ru.ari.sharing.presentation.contract.SharingScreenAction
import ru.ari.sharing.presentation.contract.SharingScreenUiState
import ru.ari.sharing.presentation.ui.components.PostCard

@Composable
internal fun SharingScreen(
    modifier: Modifier = Modifier,
    uiState: SharingScreenUiState,
    onAction: (SharingScreenAction) -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(uiState.isRefreshing),
        modifier = modifier,
        onRefresh = { onAction(SharingScreenAction.RefreshPosts) }
    ) {
        val displayPosts = if (uiState.isLoading) {
            listOf(DefaultPost, DefaultPost, DefaultPost)
        } else {
            uiState.posts
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                displayPosts.forEach { post ->
                    PostCard(
                        post = post,
                        navigateToDetailsScreen = {
                            onAction(SharingScreenAction.OpenPostDetails(it))
                        },
                        onBookingButtonClick = {
                            onAction(SharingScreenAction.BookItem(it))
                        }
                    )
                }
            }

            if (uiState.isLoading && uiState.posts.isEmpty()) {
                WiseDubsProgressIndicator(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
