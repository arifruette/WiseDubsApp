package ru.ari.sharing.presentation.viewmodel

import androidx.compose.runtime.Stable
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
import ru.ari.sharing.presentation.contract.SharingScreenAction
import ru.ari.sharing.presentation.contract.SharingScreenUiEffect
import ru.ari.sharing.presentation.contract.SharingScreenUiState
import javax.inject.Inject

@Stable
class SharingViewModel @Inject constructor(
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(SharingScreenUiState())
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
        _uiState.update {
            it.copy(
                isLoading = !isRefresh,
                isRefreshing = isRefresh
            )
        }
        viewModelScope.launch {
            when (val result = sharingInteractor.getPosts(forceRefresh = isRefresh)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            posts = result.data
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                    _uiEffect.emit(SharingScreenUiEffect.ShowError(result.message))
                }

                is Result.Exception -> {
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                    _uiEffect.emit(
                        SharingScreenUiEffect.ShowError(
                            result.error.message ?: "Неожиданная ошибка"
                        )
                    )
                }
            }
        }
    }
}
