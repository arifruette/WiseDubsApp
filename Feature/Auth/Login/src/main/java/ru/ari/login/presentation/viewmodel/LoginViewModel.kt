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
import ru.ari.login.presentation.contract.LoginScreenAction
import ru.ari.login.presentation.contract.LoginScreenUiEffect
import ru.ari.login.presentation.contract.LoginScreenUiState
import ru.ari.network.domain.models.onError
import ru.ari.network.domain.models.onException
import ru.ari.network.domain.models.onSuccess
import ru.ari.network.domain.models.toUserErrorMessage
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

            is LoginScreenAction.NavigateToRegistrationScreen -> viewModelScope.launch {
                _uiEffect.emit(LoginScreenUiEffect.NavigateToRegistrationScreen)
            }
        }
    }

    private fun loginUser() {
        val email = uiState.value.emailText.trim()
        val password = uiState.value.passwordText
        val validationError = validateCredentials(email, password)
        if (validationError != null) {
            viewModelScope.launch {
                _uiEffect.emit(LoginScreenUiEffect.ShowError(validationError))
            }
            return
        }

        _uiState.update {
            _uiState.value.copy(isLoading = true)
        }
        viewModelScope.launch {
            val result =
                authInteractor.login(email, password)
            result.onSuccess {
                _uiEffect.emit(LoginScreenUiEffect.NavigateToMainScreen)
                _uiState.update { it.copy(isLoading = false) }
            }.onError { _, message ->
                _uiEffect.emit(LoginScreenUiEffect.ShowError(message))
                _uiState.update { it.copy(isLoading = false) }
            }.onException {
                _uiEffect.emit(LoginScreenUiEffect.ShowError(it.toUserErrorMessage()))
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

    private fun validateCredentials(email: String, password: String): String? =
        when {
            !EMAIL_REGEX.matches(email) -> EMAIL_FORMAT_ERROR
            password.length < MIN_PASSWORD_LENGTH -> INVALID_CREDENTIALS_ERROR
            else -> null
        }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        const val MIN_PASSWORD_LENGTH = 5
        const val EMAIL_FORMAT_ERROR = "Неверный формат почты"
        const val INVALID_CREDENTIALS_ERROR = "Неверный логин или пароль"
    }
}
