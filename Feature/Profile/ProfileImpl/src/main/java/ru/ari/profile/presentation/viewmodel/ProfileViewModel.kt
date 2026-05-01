package ru.ari.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.auth.common.api.domain.interactor.AuthInteractor
import ru.ari.auth.common.api.domain.models.UserProfile
import ru.ari.network.domain.models.Result
import ru.ari.profile.presentation.contract.ProfileScreenAction
import ru.ari.profile.presentation.contract.ProfileScreenUiEffect
import ru.ari.profile.presentation.contract.ProfileScreenUiState
import ru.ari.profile.presentation.models.ProfileUiModel

class ProfileViewModel @Inject constructor(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ProfileScreenUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onAction(action: ProfileScreenAction) {
        when (action) {
            ProfileScreenAction.Load -> loadProfile()
            ProfileScreenAction.RetryProfile -> loadProfile()
            ProfileScreenAction.ClickReservedPosts -> emitEffect(ProfileScreenUiEffect.OpenReservedPosts)
            ProfileScreenAction.ClickLogout -> {
                _uiState.update { it.copy(showLogoutDialog = true) }
            }
            ProfileScreenAction.DismissLogout -> {
                _uiState.update { it.copy(showLogoutDialog = false) }
            }
            ProfileScreenAction.ConfirmLogout -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isProfileLoading = true,
                    profileError = null
                )
            }
            when (val result = authInteractor.getCurrentUser()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isProfileLoading = false,
                            profile = result.data.toUiModel(),
                            profileError = null
                        )
                    }
                }
                is Result.Error -> showProfileError(result.message)
                is Result.Exception -> showProfileError(result.error.message ?: "Не удалось загрузить профиль")
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(showLogoutDialog = false) }
            runCatching { authInteractor.logout() }
                .onSuccess { _uiEffect.emit(ProfileScreenUiEffect.NavigateToLogin) }
                .onFailure {
                    _uiEffect.emit(ProfileScreenUiEffect.ShowError("Не удалось выйти из аккаунта"))
                }
        }
    }

    private fun showProfileError(message: String) {
        _uiState.update {
            it.copy(
                isProfileLoading = false,
                profileError = message
            )
        }
    }

    private fun emitEffect(effect: ProfileScreenUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }

    private fun UserProfile.toUiModel(): ProfileUiModel =
        ProfileUiModel(
            email = email,
            telegramId = telegramId
        )
}
