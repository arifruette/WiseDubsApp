package ru.ari.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.interactor.PostsInteractor
import ru.ari.posts.api.domain.models.PickupLocation
import ru.ari.posts.api.domain.models.Post
import ru.ari.profile.presentation.contract.ReservedPostsScreenAction
import ru.ari.profile.presentation.contract.ReservedPostsScreenUiEffect
import ru.ari.profile.presentation.contract.ReservedPostsScreenUiState
import ru.ari.profile.presentation.models.ReservedPostUiModel

class ReservedPostsViewModel @Inject constructor(
    private val postsInteractor: PostsInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservedPostsScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ReservedPostsScreenUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onAction(action: ReservedPostsScreenAction) {
        when (action) {
            ReservedPostsScreenAction.Load -> loadReservedPosts()
            ReservedPostsScreenAction.Retry -> loadReservedPosts()
            ReservedPostsScreenAction.ClickBack -> emitEffect(ReservedPostsScreenUiEffect.NavigateBack)
            ReservedPostsScreenAction.ClickFindPosts -> emitEffect(ReservedPostsScreenUiEffect.OpenSharing)
            is ReservedPostsScreenAction.ClickReservedPost -> {
                emitEffect(ReservedPostsScreenUiEffect.OpenPostDetails(action.postId))
            }
        }
    }

    private fun loadReservedPosts() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }
            when (val result = postsInteractor.getReservedPosts(forceRefresh = true)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            reservedPosts = result.data.map { post -> post.toReservedUiModel() }.toImmutableList(),
                            error = null
                        )
                    }
                }
                is Result.Error -> showError(result.message)
                is Result.Exception -> showError(result.error.message ?: "Не удалось загрузить забронированные посты")
            }
        }
    }

    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                error = message
            )
        }
    }

    private fun emitEffect(effect: ReservedPostsScreenUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }

    private fun Post.toReservedUiModel(): ReservedPostUiModel =
        ReservedPostUiModel(
            id = id,
            title = title,
            description = description,
            exchange = exchange.ifBlank { "Не указан" },
            pickupLocation = pickupLocation.toDisplayText()
        )

    private fun PickupLocation.toDisplayText(): String =
        listOfNotNull(
            corpus?.takeIf(String::isNotBlank)?.let { "корпус $it" },
            room.takeIf(String::isNotBlank)?.let { "ауд. $it" }
        ).joinToString(", ").ifBlank { "Место не указано" }
}
