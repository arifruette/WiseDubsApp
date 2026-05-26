package ru.ari.sharing.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.cache.domain.datastore.DataStoreHelper
import ru.ari.network.domain.models.Result
import ru.ari.network.domain.models.toUserErrorMessage
import ru.ari.sharing.api.domain.interactor.SharingInteractor
import ru.ari.sharing.domain.mapper.SharingPostUiMapper
import ru.ari.sharing.presentation.contract.SharingScreenAction
import ru.ari.sharing.presentation.contract.SharingScreenUiEffect
import ru.ari.sharing.presentation.contract.SharingScreenUiState

class SharingViewModel @Inject constructor(
    private val sharingInteractor: SharingInteractor,
    private val dataStoreHelper: DataStoreHelper,
    private val sharingPostUiMapper: SharingPostUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<SharingScreenUiState>(SharingScreenUiState.Loading)
    val uiState: StateFlow<SharingScreenUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SharingScreenUiEffect>()
    val uiEffect: SharedFlow<SharingScreenUiEffect> = _uiEffect.asSharedFlow()

    private var currentUserId: Long? = null
    private var hasResolvedInitialLoad = false

    init {
        observePosts()
    }

    private fun observePosts() {
        viewModelScope.launch {
            val session = dataStoreHelper.getSessionState().firstOrNull()
            currentUserId = session?.userId
            sharingInteractor.observePosts().collect { posts ->
                if (posts.isEmpty() && _uiState.value is SharingScreenUiState.Loading && !hasResolvedInitialLoad) {
                    return@collect
                }
                publishPosts(posts, currentUserId)
            }
        }
    }

    fun onAction(action: SharingScreenAction) {
        when (action) {
            SharingScreenAction.LoadPosts -> loadPosts(mode = LoadMode.Initial)
            SharingScreenAction.RefreshPosts -> loadPosts(mode = LoadMode.UserRefresh)
            SharingScreenAction.SilentRefreshPosts -> loadPosts(mode = LoadMode.SilentRefresh)
            SharingScreenAction.RetryLoadPosts -> loadPosts(mode = LoadMode.Initial)
            is SharingScreenAction.OpenPostDetails -> emitOpenDetails(
                postId = action.postId,
                autoReserve = false
            )
            is SharingScreenAction.BookItem -> emitOpenDetails(
                postId = action.postId,
                autoReserve = true
            )
        }
    }

    private fun emitOpenDetails(postId: Long, autoReserve: Boolean) {
        viewModelScope.launch {
            _uiEffect.emit(
                SharingScreenUiEffect.NavigateToDetails(
                    postId = postId,
                    autoReserve = autoReserve
                )
            )
        }
    }

    private fun loadPosts(mode: LoadMode) {
        if (mode == LoadMode.Initial && _uiState.value is SharingScreenUiState.Content) {
            // Already have content, downgrade Initial to SilentRefresh to avoid shimmer
            loadPosts(LoadMode.SilentRefresh)
            return
        }

        when (mode) {
            LoadMode.Initial -> {
                if (_uiState.value !is SharingScreenUiState.Content) {
                    _uiState.value = SharingScreenUiState.Loading
                }
            }
            LoadMode.UserRefresh -> markRefreshing()
            LoadMode.SilentRefresh -> Unit
        }

        viewModelScope.launch {
            when (val result = sharingInteractor.getPosts(forceRefresh = mode.forceRefresh)) {
                is Result.Success -> {
                    hasResolvedInitialLoad = true
                    publishPosts(result.data, currentUserId)
                    clearRefreshState()
                }

                is Result.Error -> {
                    handleLoadError(mode, result.message)
                }

                is Result.Exception -> {
                    handleLoadError(mode, result.error.toUserErrorMessage())
                }
            }
        }
    }

    private fun handleLoadError(mode: LoadMode, message: String) {
        clearRefreshState()
        if (mode != LoadMode.SilentRefresh) {
            viewModelScope.launch {
                _uiEffect.emit(SharingScreenUiEffect.ShowError(message))
            }
        }
        if (mode == LoadMode.Initial && _uiState.value is SharingScreenUiState.Loading) {
            hasResolvedInitialLoad = true
            _uiState.value = SharingScreenUiState.Empty(isRefreshing = false)
        }
    }

    private fun publishPosts(
        posts: List<ru.ari.posts.api.domain.models.Post>,
        currentUserId: Long?
    ) {
        val uiPosts = sharingPostUiMapper.map(
            posts = posts,
            currentUserId = currentUserId
        )
        _uiState.update { state ->
            if (uiPosts.isEmpty()) {
                SharingScreenUiState.Empty(isRefreshing = state.isRefreshing())
            } else {
                SharingScreenUiState.Content(
                    posts = uiPosts,
                    isRefreshing = state.isRefreshing()
                )
            }
        }
    }

    private fun SharingScreenUiState.isRefreshing(): Boolean = when (this) {
        is SharingScreenUiState.Content -> isRefreshing
        is SharingScreenUiState.Empty -> isRefreshing
        SharingScreenUiState.Loading -> false
    }

    private fun markRefreshing() {
        _uiState.update { state ->
            when (state) {
                is SharingScreenUiState.Content -> state.copy(isRefreshing = true)
                is SharingScreenUiState.Empty -> state.copy(isRefreshing = true)
                SharingScreenUiState.Loading -> state
            }
        }
    }

    private fun clearRefreshState() {
        _uiState.update { state ->
            when (state) {
                is SharingScreenUiState.Content -> state.copy(isRefreshing = false)
                is SharingScreenUiState.Empty -> state.copy(isRefreshing = false)
                SharingScreenUiState.Loading -> state
            }
        }
    }

    private enum class LoadMode(
        val forceRefresh: Boolean
    ) {
        Initial(forceRefresh = false),
        UserRefresh(forceRefresh = true),
        SilentRefresh(forceRefresh = true)
    }
}
