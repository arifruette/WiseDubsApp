package ru.ari.myposts.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.ari.myposts.presentation.contract.MyPostsActionHandler
import ru.ari.myposts.presentation.contract.MyPostsScreenAction
import ru.ari.myposts.presentation.contract.MyPostsScreenUiState
import ru.ari.myposts.presentation.contract.MyPostsTab
import ru.ari.myposts.presentation.models.MyPostUiModel
import ru.ari.myposts.presentation.ui.components.MyPostCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsScreen(
    uiState: MyPostsScreenUiState,
    actionHandler: MyPostsActionHandler,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Text(
                text = "My posts",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { actionHandler.onAction(MyPostsScreenAction.ClickCreate) },
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create post"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PrimaryTabRow(selectedTabIndex = uiState.selectedTab.ordinal) {
                MyPostsTab.entries.forEach { tab ->
                    Tab(
                        selected = tab == uiState.selectedTab,
                        onClick = { actionHandler.onAction(MyPostsScreenAction.SelectTab(tab)) },
                        text = {
                            Text(
                                text = tab.title(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = (uiState as? MyPostsScreenUiState.Content)?.isRefreshing == true,
                onRefresh = { actionHandler.onAction(MyPostsScreenAction.Refresh) },
                state = rememberPullToRefreshState(),
                modifier = Modifier.fillMaxSize()
            ) {
                when (uiState) {
                    is MyPostsScreenUiState.Loading -> EmptyState(
                        text = "Loading posts...",
                        modifier = Modifier.fillMaxSize()
                    )

                    is MyPostsScreenUiState.Empty -> EmptyState(
                        text = if (uiState.selectedTab == MyPostsTab.Active) {
                            "You have no active posts yet"
                        } else {
                            "You have no inactive posts yet"
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    is MyPostsScreenUiState.Content -> PostsList(
                        posts = uiState.posts,
                        actionHandler = actionHandler,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun PostsList(
    posts: ImmutableList<MyPostUiModel>,
    actionHandler: MyPostsActionHandler,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(items = posts, key = MyPostUiModel::id) { post ->
            MyPostCard(
                post = post,
                actionHandler = actionHandler
            )
        }
    }
}

@Composable
private fun EmptyState(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun MyPostsTab.title(): String = when (this) {
    MyPostsTab.Active -> "Active"
    MyPostsTab.Inactive -> "Inactive"
}
