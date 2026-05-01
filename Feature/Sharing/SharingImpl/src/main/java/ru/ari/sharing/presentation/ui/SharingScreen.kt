@file:Suppress("DEPRECATION")

package ru.ari.sharing.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    val isRefreshing = when (uiState) {
        is SharingScreenUiState.Content -> uiState.isRefreshing
        is SharingScreenUiState.Empty -> uiState.isRefreshing
        SharingScreenUiState.Loading -> false
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        modifier = modifier,
        onRefresh = { onAction(SharingScreenAction.RefreshPosts) }
    ) {
        when (uiState) {
            SharingScreenUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                ) {
                    repeat(5) {
                        PostCardShimmer()
                    }
                }
            }

            is SharingScreenUiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Постов пока нет",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            is SharingScreenUiState.Content -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    items(
                        items = uiState.posts,
                        key = { post -> post.id }
                    ) { post ->
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
