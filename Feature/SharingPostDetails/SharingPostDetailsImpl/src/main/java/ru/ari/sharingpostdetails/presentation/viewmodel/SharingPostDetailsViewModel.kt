package ru.ari.sharingpostdetails.presentation.viewmodel

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
import kotlinx.coroutines.launch
import ru.ari.cache.domain.datastore.DataStoreHelper
import ru.ari.network.domain.models.Result
import ru.ari.network.domain.models.toUserErrorMessage
import ru.ari.posts.api.domain.interactor.PostsInteractor
import ru.ari.posts.api.domain.models.Post
import ru.ari.sharingpostdetails.presentation.contract.SharingPostDetailsUiAction
import ru.ari.sharingpostdetails.presentation.contract.SharingPostDetailsUiEffect
import ru.ari.sharingpostdetails.presentation.contract.SharingPostDetailsUiState
import ru.ari.sharingpostdetails.presentation.mappers.SharingPostDetailsUiMapper
import ru.ari.sharingpostdetails.presentation.models.PrimaryActionType

class SharingPostDetailsViewModel @Inject constructor(
    private val postsInteractor: PostsInteractor,
    private val dataStoreHelper: DataStoreHelper,
    private val uiMapper: SharingPostDetailsUiMapper
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<SharingPostDetailsUiState>(SharingPostDetailsUiState.Loading)
    val uiState: StateFlow<SharingPostDetailsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SharingPostDetailsUiEffect>()
    val uiEffect: SharedFlow<SharingPostDetailsUiEffect> = _uiEffect.asSharedFlow()

    private var currentPost: Post? = null
    private var currentUserId: Long? = null
    private var currentPostId: Long? = null
    private var pendingAutoReservePostId: Long? = null

    fun onAction(action: SharingPostDetailsUiAction) {
        when (action) {
            is SharingPostDetailsUiAction.Load -> {
                if (action.autoReserve) {
                    pendingAutoReservePostId = action.postId
                }
                if (shouldLoad(action.postId)) {
                    load(action.postId)
                } else {
                    performPendingAutoReserve()
                }
            }

            SharingPostDetailsUiAction.ClickPrimaryAction -> performPrimaryAction()
            SharingPostDetailsUiAction.ClickContact -> openTelegram()
            is SharingPostDetailsUiAction.ClickImage -> selectImage(action.index)
            SharingPostDetailsUiAction.DismissImageViewer -> dismissImageViewer()
        }
    }

    private fun shouldLoad(postId: Long): Boolean {
        return _uiState.value is SharingPostDetailsUiState.Loading || currentPostId != postId
    }

    private fun load(postId: Long) {
        currentPostId = postId
        _uiState.value = SharingPostDetailsUiState.Loading

        viewModelScope.launch {
            currentUserId = dataStoreHelper.getSessionState().firstOrNull()?.userId
            when (val result = postsInteractor.getPostById(postId)) {
                is Result.Success -> {
                    currentPost = result.data
                    publish(isPrimaryActionInProgress = false)
                    performPendingAutoReserve()
                }

                is Result.Error -> {
                    emitEffect(SharingPostDetailsUiEffect.CloseWithError(result.message))
                }

                is Result.Exception -> {
                    emitEffect(
                        SharingPostDetailsUiEffect.CloseWithError(
                            result.error.toUserErrorMessage("Не удалось загрузить детали поста")
                        )
                    )
                }
            }
        }
    }

    private fun performPrimaryAction() {
        val post = currentPost ?: return
        val content = _uiState.value as? SharingPostDetailsUiState.Content ?: return
        if (content.isPrimaryActionInProgress) {
            return
        }

        when (content.post.primaryAction.type) {
            PrimaryActionType.RESERVE -> mutateReservation { postsInteractor.reservePost(post.id) }
            PrimaryActionType.UNRESERVE -> mutateReservation { postsInteractor.unreservePost(post.id) }
            PrimaryActionType.NONE -> Unit
        }
    }

    private fun performPendingAutoReserve() {
        val post = currentPost ?: return
        if (pendingAutoReservePostId != post.id) {
            return
        }
        pendingAutoReservePostId = null

        val content = _uiState.value as? SharingPostDetailsUiState.Content ?: return
        when (content.post.primaryAction.type) {
            PrimaryActionType.RESERVE -> performPrimaryAction()
            PrimaryActionType.UNRESERVE -> {
                emitEffect(SharingPostDetailsUiEffect.ShowMessage("Пост уже забронирован вами"))
            }
            PrimaryActionType.NONE -> {
                emitEffect(SharingPostDetailsUiEffect.ShowMessage("Пост уже занят"))
            }
        }
    }

    private fun mutateReservation(
        request: suspend () -> Result<Post>
    ) {
        updateContent { copy(isPrimaryActionInProgress = true) }

        viewModelScope.launch {
            when (val result = request()) {
                is Result.Success -> {
                    currentPost = result.data
                    publish(isPrimaryActionInProgress = false)
                }

                is Result.Error -> {
                    updateContent { copy(isPrimaryActionInProgress = false) }
                    emitEffect(SharingPostDetailsUiEffect.ShowMessage(result.message))
                }

                is Result.Exception -> {
                    updateContent { copy(isPrimaryActionInProgress = false) }
                    emitEffect(
                        SharingPostDetailsUiEffect.ShowMessage(
                            result.error.toUserErrorMessage("Не удалось обновить бронь")
                        )
                    )
                }
            }
        }
    }

    private fun openTelegram() {
        val telegramId = currentPost?.authorTelegramId?.trim().orEmpty()
        if (telegramId.isBlank()) {
            emitEffect(SharingPostDetailsUiEffect.ShowMessage("Telegram автора недоступен"))
            return
        }
        emitEffect(SharingPostDetailsUiEffect.OpenTelegram(telegramId))
    }

    private fun selectImage(index: Int) {
        updateContent { copy(selectedImageIndex = index) }
    }

    private fun dismissImageViewer() {
        updateContent { copy(selectedImageIndex = null) }
    }

    private fun publish(isPrimaryActionInProgress: Boolean) {
        val post = currentPost ?: return
        val previousContent = _uiState.value as? SharingPostDetailsUiState.Content
        _uiState.value = SharingPostDetailsUiState.Content(
            post = uiMapper.map(
                post = post,
                currentUserId = currentUserId,
                isPrimaryActionInProgress = isPrimaryActionInProgress
            ),
            selectedImageIndex = previousContent?.selectedImageIndex,
            isPrimaryActionInProgress = isPrimaryActionInProgress
        )
    }

    private fun updateContent(
        transform: SharingPostDetailsUiState.Content.() -> SharingPostDetailsUiState.Content
    ) {
        val content = _uiState.value as? SharingPostDetailsUiState.Content ?: return
        val updatedContent = content.transform()
        val post = currentPost ?: run {
            _uiState.value = updatedContent
            return
        }
        _uiState.value = updatedContent.copy(
            post = uiMapper.map(
                post = post,
                currentUserId = currentUserId,
                isPrimaryActionInProgress = updatedContent.isPrimaryActionInProgress
            )
        )
    }

    private fun emitEffect(effect: SharingPostDetailsUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }
}
