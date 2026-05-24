package ru.ari.registration.presentation.viewmodel

import android.util.Log
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
import ru.ari.auth.common.api.domain.interactor.AuthInteractor
import ru.ari.auth.common.api.domain.models.params.UserRegisterParams
import ru.ari.network.domain.models.onError
import ru.ari.network.domain.models.onException
import ru.ari.network.domain.models.onSuccess
import ru.ari.registration.domain.usecase.ValidateUserCredentialsUseCase
import ru.ari.registration.presentation.contract.RegistrationScreenAction
import ru.ari.registration.presentation.models.PasswordField
import ru.ari.registration.presentation.contract.RegistrationScreenUiEffect
import ru.ari.registration.presentation.contract.RegistrationScreenUiState
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val validateUserCredentialsUseCase: ValidateUserCredentialsUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<RegistrationScreenUiState> = MutableStateFlow(
        RegistrationScreenUiState()
    )
    val uiState: StateFlow<RegistrationScreenUiState> = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<RegistrationScreenUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<RegistrationScreenUiEffect> = _uiEffect.asSharedFlow()

    fun onAction(action: RegistrationScreenAction) {
        when (action) {
            is RegistrationScreenAction.ChangeEmailState -> changeEmailState(action.email)
            is RegistrationScreenAction.ChangePasswordState -> changePasswordState(
                action.password,
                action.passwordField
            )

            is RegistrationScreenAction.ChangePasswordVisibility -> changePasswordVisibility(action.passwordField)
            is RegistrationScreenAction.ChangeTelegramIdState -> changeTelegramIdState(action.telegramId)
            is RegistrationScreenAction.RegisterUser -> registerUser()
            is RegistrationScreenAction.NavigateToLoginScreen -> viewModelScope.launch {
                _uiEffect.emit(RegistrationScreenUiEffect.NavigateToLoginScreen)
            }
        }
    }

    private fun registerUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            if (validateUserCredentials()) {
                val result = authInteractor.registerAndLogin(
                    user = UserRegisterParams(
                        email = _uiState.value.emailText,
                        telegramId = _uiState.value.telegramIdText,
                        password = _uiState.value.firstPasswordText
                    )
                )
                result.onSuccess {
                    Log.d("SUCCESS", "registerUser: $it")
                    _uiEffect.emit(RegistrationScreenUiEffect.NavigateToMainScreen)
                    _uiState.update { it.copy(isLoading = false) }
                }.onError { code, message ->
                    Log.e("ERROR", "registerUser: $code $message")
                    _uiEffect.emit(RegistrationScreenUiEffect.ShowError(message))
                    _uiState.update { it.copy(isLoading = false) }
                }.onException {
                    Log.e("ERROR", "registerUser: ${it.message}")
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun validateUserCredentials(): Boolean {
        val state = _uiState.value
        return validateUserCredentialsUseCase(
            state.emailText,
            state.firstPasswordText,
            state.secondPasswordText
        ) { message ->
            if (message != null) {
                _uiEffect.emit(
                    RegistrationScreenUiEffect.ShowError(
                        message
                    )
                )
            }

        }
    }

    private fun changeEmailState(newEmail: String) =
        _uiState.update { it.copy(emailText = newEmail) }

    private fun changeTelegramIdState(newTelegramId: String) =
        _uiState.update { it.copy(telegramIdText = newTelegramId) }

    private fun changePasswordState(newPasswordText: String, passwordField: PasswordField) =
        _uiState.update {
            when (passwordField) {
                PasswordField.FIRST -> it.copy(firstPasswordText = newPasswordText)
                PasswordField.SECOND -> it.copy(secondPasswordText = newPasswordText)
            }
        }


    private fun changePasswordVisibility(passwordField: PasswordField) {
        _uiState.update {
            when (passwordField) {
                PasswordField.FIRST -> it.copy(isFirstPasswordVisible = !_uiState.value.isFirstPasswordVisible)
                PasswordField.SECOND -> it.copy(isSecondPasswordVisible = !_uiState.value.isSecondPasswordVisible)
            }
        }
    }
}