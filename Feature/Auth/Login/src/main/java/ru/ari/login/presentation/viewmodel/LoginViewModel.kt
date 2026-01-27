package ru.ari.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.auth.common.api.domain.interactor.AuthInteractor
import ru.ari.login.presentation.viewmodel.contract.LoginScreenAction
import ru.ari.login.presentation.viewmodel.contract.LoginScreenUiEffect
import ru.ari.login.presentation.viewmodel.contract.LoginScreenUiState
import ru.ari.network.domain.models.onError
import ru.ari.network.domain.models.onException
import ru.ari.network.domain.models.onSuccess
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginScreenUiState> =
        MutableStateFlow(LoginScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<LoginScreenUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<LoginScreenUiEffect> = _uiEffect.asSharedFlow()

    fun onAction(action: LoginScreenAction) {
        when (action) {
            is LoginScreenAction.LoginUser -> loginUser()

            is LoginScreenAction.ChangeEmailState -> changeLoginState(action.email)

            is LoginScreenAction.ChangePasswordState -> changePasswordState(action.password)

            is LoginScreenAction.ChangePasswordVisibility -> changePasswordVisibility()
        }
    }

    private fun loginUser() {
        _uiState.update {
            _uiState.value.copy(isLoading = true)
        }
        viewModelScope.launch {
            val result =
                authInteractor.login(uiState.value.emailText, uiState.value.passwordText)
            result.onSuccess {
                _uiEffect.emit(LoginScreenUiEffect.NavigateToMainScreen)
                _uiState.update { it.copy(isLoading = false) }
            }.onError { code, message ->
                _uiEffect.emit(LoginScreenUiEffect.ShowError(message))
                _uiState.update { it.copy(isLoading = false) }
            }.onException {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun changePasswordState(newPassword: String) = _uiState.update {
        it.copy(passwordText = newPassword)
    }

    private fun changeLoginState(newLogin: String) = _uiState.update {
        it.copy(emailText = newLogin)
    }

    private fun changePasswordVisibility() = _uiState.update {
        it.copy(isPasswordTextVisible = !it.isPasswordTextVisible)
    }
}