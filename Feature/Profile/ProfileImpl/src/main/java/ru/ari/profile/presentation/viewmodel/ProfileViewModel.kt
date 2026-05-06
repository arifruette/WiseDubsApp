package ru.ari.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.Job
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

    private var refreshJob: Job? = null

    init {
        observeProfile()
        refreshProfile()
    }

    fun onAction(action: ProfileScreenAction) {
        when (action) {
            ProfileScreenAction.Load -> Unit
            ProfileScreenAction.RetryProfile -> refreshProfile()
            ProfileScreenAction.ClickReservedPosts -> emitEffect(ProfileScreenUiEffect.OpenReservedPosts)
            ProfileScreenAction.ClickMyBookings -> emitEffect(ProfileScreenUiEffect.OpenMyBookings)
            ProfileScreenAction.ClickLogout -> {
                _uiState.update { it.copy(showLogoutDialog = true) }
            }
            ProfileScreenAction.DismissLogout -> {
                _uiState.update { it.copy(showLogoutDialog = false) }
            }
            ProfileScreenAction.ConfirmLogout -> logout()
        }
    }

    private fun observeProfile() {
        viewModelScope.launch {
            authInteractor.observeCurrentUser().collect { profile ->
                _uiState.update {
                    if (profile == null) {
                        it.copy(
                            profile = null,
                            isProfileLoading = false,
                            profileError = null,
                            showLogoutDialog = false
                        )
                    } else {
                        it.copy(
                            profile = profile.toUiModel(),
                            isProfileLoading = false,
                            profileError = null,
                            isLogoutLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun refreshProfile() {
        if (refreshJob?.isActive == true) return

        refreshJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isProfileLoading = it.profile == null,
                    profileError = null
                )
            }
            when (val result = authInteractor.refreshCurrentUser()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isProfileLoading = false,
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
            _uiState.update {
                it.copy(
                    showLogoutDialog = false,
                    isLogoutLoading = true
                )
            }
            runCatching { authInteractor.logout() }
                .onFailure {
                    _uiState.update { state -> state.copy(isLogoutLoading = false) }
                    _uiEffect.emit(ProfileScreenUiEffect.ShowError("Не удалось выйти из аккаунта"))
                }
        }
    }

    private fun showProfileError(message: String) {
        val hasCachedProfile = _uiState.value.profile != null
        if (hasCachedProfile) {
            _uiState.update {
                it.copy(
                    isProfileLoading = false,
                    profileError = null
                )
            }
            emitEffect(ProfileScreenUiEffect.ShowError(message))
        } else {
            _uiState.update {
                it.copy(
                    isProfileLoading = false,
                    profileError = message
                )
            }
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
