package ru.ari.sharing.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.network.domain.models.Result
import ru.ari.sharing.api.domain.interactor.SharingInteractor
import ru.ari.sharing.domain.mapper.SharingPostUiMapper
import ru.ari.sharing.presentation.contract.SharingScreenAction
import ru.ari.sharing.presentation.contract.SharingScreenUiEffect
import ru.ari.sharing.presentation.contract.SharingScreenUiState
import javax.inject.Inject

class SharingViewModel @Inject constructor(
    private val sharingInteractor: SharingInteractor,
    private val sharingPostUiMapper: SharingPostUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<SharingScreenUiState>(SharingScreenUiState.Loading)
    val uiState: StateFlow<SharingScreenUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SharingScreenUiEffect>()
    val uiEffect: SharedFlow<SharingScreenUiEffect> = _uiEffect.asSharedFlow()

    fun onAction(action: SharingScreenAction) {
        when (action) {
            SharingScreenAction.LoadPosts -> loadPosts(isRefresh = false)
            SharingScreenAction.RefreshPosts -> loadPosts(isRefresh = true)
            SharingScreenAction.RetryLoadPosts -> loadPosts(isRefresh = false)
            is SharingScreenAction.OpenPostDetails -> viewModelScope.launch {
                _uiEffect.emit(SharingScreenUiEffect.NavigateToDetails(action.postId))
            }
            is SharingScreenAction.BookItem -> viewModelScope.launch {
                _uiEffect.emit(SharingScreenUiEffect.ShowError("Бронирование будет доступно позже"))
            }
        }
    }

    private fun loadPosts(isRefresh: Boolean) {
        if (isRefresh) {
            markRefreshing()
        } else {
            _uiState.value = SharingScreenUiState.Loading
        }

        viewModelScope.launch {
            when (val result = sharingInteractor.getPosts(forceRefresh = isRefresh)) {
                is Result.Success -> {
                    val uiPosts = sharingPostUiMapper.map(result.data)
                    _uiState.value = if (uiPosts.isEmpty()) {
                        SharingScreenUiState.Empty
                    } else {
                        SharingScreenUiState.Content(posts = uiPosts, isRefreshing = false)
                    }
                }

                is Result.Error -> {
                    clearRefreshAfterFailure(isRefresh)
                    _uiEffect.emit(SharingScreenUiEffect.ShowError(result.message))
                }

                is Result.Exception -> {
                    clearRefreshAfterFailure(isRefresh)
                    _uiEffect.emit(
                        SharingScreenUiEffect.ShowError(
                            result.error.message ?: "Неожиданная ошибка"
                        )
                    )
                }
            }
        }
    }

    private fun markRefreshing() {
        _uiState.update { state ->
            when (state) {
                is SharingScreenUiState.Content -> state.copy(isRefreshing = true)
                else -> SharingScreenUiState.Loading
            }
        }
    }

    private fun clearRefreshAfterFailure(isRefresh: Boolean) {
        _uiState.update { state ->
            when {
                isRefresh && state is SharingScreenUiState.Content -> state.copy(isRefreshing = false)
                else -> SharingScreenUiState.Empty
            }
        }
    }
}
