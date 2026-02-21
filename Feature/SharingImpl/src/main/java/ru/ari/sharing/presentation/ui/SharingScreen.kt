@file:Suppress("DEPRECATION")

package ru.ari.sharing.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.ari.sharing.presentation.contract.SharingScreenAction
import ru.ari.sharing.presentation.contract.SharingScreenUiState
import ru.ari.sharing.presentation.ui.components.PostCard
import ru.ari.sharing.presentation.ui.components.PostCardShimmer

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
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                ) {
                    repeat(3) {
                        PostCardShimmer()
                    }
                }
            }

            uiState.posts.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Постов пока нет",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                ) {
                    uiState.posts.forEach { post ->
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
            }
        }
    }
}
