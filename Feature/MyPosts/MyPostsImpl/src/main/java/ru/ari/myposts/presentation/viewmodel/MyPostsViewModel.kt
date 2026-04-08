package ru.ari.myposts.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.myposts.domain.mapper.MyPostsUiMapper
import ru.ari.myposts.presentation.contract.MyPostsScreenAction
import ru.ari.myposts.presentation.contract.MyPostsScreenUiEffect
import ru.ari.myposts.presentation.contract.MyPostsScreenUiState
import ru.ari.myposts.presentation.contract.MyPostsTab
import ru.ari.myposts.presentation.models.MyPostUiModel
import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.interactor.PostsInteractor
import javax.inject.Inject

class MyPostsViewModel @Inject constructor(
    private val postsInteractor: PostsInteractor,
    private val myPostsUiMapper: MyPostsUiMapper
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MyPostsScreenUiState>(MyPostsScreenUiState.Loading())
    val uiState: StateFlow<MyPostsScreenUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MyPostsScreenUiEffect>()
    val uiEffect: SharedFlow<MyPostsScreenUiEffect> = _uiEffect.asSharedFlow()

    private var allPosts: ImmutableList<MyPostUiModel> = persistentListOf()
    private var hasLoaded = false

    fun onAction(action: MyPostsScreenAction) {
        when (action) {
            MyPostsScreenAction.Load -> {
                if (!hasLoaded) {
                    loadPosts(forceRefresh = false)
                }
            }

            MyPostsScreenAction.Refresh -> loadPosts(forceRefresh = true)
            MyPostsScreenAction.ClickCreate -> emitEffect(MyPostsScreenUiEffect.OpenCreatePost)
            is MyPostsScreenAction.ClickPost ->
                emitEffect(MyPostsScreenUiEffect.OpenEditPost(action.postId))

            is MyPostsScreenAction.SelectTab -> selectTab(action.tab)
        }
    }

    private fun loadPosts(forceRefresh: Boolean) {
        val selectedTab = _uiState.value.selectedTab
        if (forceRefresh) {
            _uiState.update { current ->
                when (current) {
                    is MyPostsScreenUiState.Content -> current.copy(isRefreshing = true)
                    is MyPostsScreenUiState.Empty -> MyPostsScreenUiState.Loading(selectedTab)
                    is MyPostsScreenUiState.Loading -> current
                }
            }
        } else {
            _uiState.value = MyPostsScreenUiState.Loading(selectedTab)
        }

        viewModelScope.launch {
            when (val result = postsInteractor.getMyPosts(forceRefresh)) {
                is Result.Success -> {
                    hasLoaded = true
                    allPosts = myPostsUiMapper.map(result.data)
                    publishState(selectedTab = selectedTab, isRefreshing = false)
                }

                is Result.Error -> {
                    publishFailure(
                        selectedTab = selectedTab,
                        isRefreshing = forceRefresh,
                        message = result.message
                    )
                }

                is Result.Exception -> {
                    publishFailure(
                        selectedTab = selectedTab,
                        isRefreshing = forceRefresh,
                        message = result.error.message
                            ?: "Непредвиденная ошибка"
                    )
                }
            }
        }
    }

    private fun publishFailure(
        selectedTab: MyPostsTab,
        isRefreshing: Boolean,
        message: String
    ) {
        publishState(selectedTab = selectedTab, isRefreshing = false)
        if (allPosts.isEmpty() && !isRefreshing) {
            _uiState.value = MyPostsScreenUiState.Empty(selectedTab)
        }
        emitEffect(MyPostsScreenUiEffect.ShowError(message))
    }

    private fun selectTab(tab: MyPostsTab) {
        val current = _uiState.value
        _uiState.value = when (current) {
            is MyPostsScreenUiState.Loading -> current.copy(selectedTab = tab)
            is MyPostsScreenUiState.Empty -> MyPostsScreenUiState.Empty(tab)
            is MyPostsScreenUiState.Content -> {
                val filteredPosts = allPosts.filtered(tab)
                if (filteredPosts.isEmpty()) {
                    MyPostsScreenUiState.Empty(tab)
                } else {
                    current.copy(selectedTab = tab, posts = filteredPosts)
                }
            }
        }
    }

    private fun publishState(selectedTab: MyPostsTab, isRefreshing: Boolean) {
        val filteredPosts = allPosts.filtered(selectedTab)
        _uiState.value = if (filteredPosts.isEmpty()) {
            MyPostsScreenUiState.Empty(selectedTab)
        } else {
            MyPostsScreenUiState.Content(
                selectedTab = selectedTab,
                posts = filteredPosts,
                isRefreshing = isRefreshing
            )
        }
    }

    private fun ImmutableList<MyPostUiModel>.filtered(tab: MyPostsTab): ImmutableList<MyPostUiModel> =
        filter { post -> post.isActive == (tab == MyPostsTab.Active) }
            .toImmutableList()

    private fun emitEffect(effect: MyPostsScreenUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }
}
