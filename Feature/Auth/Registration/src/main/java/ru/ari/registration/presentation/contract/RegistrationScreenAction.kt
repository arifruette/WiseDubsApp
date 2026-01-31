package ru.ari.registration.presentation.contract

import androidx.compose.runtime.Stable
import ru.ari.registration.presentation.models.PasswordField

@Stable
sealed interface RegistrationScreenAction {
    data object RegisterUser : RegistrationScreenAction
    data class ChangeEmailState(val email: String) : RegistrationScreenAction
    data class ChangePasswordState(val password: String, val passwordField: PasswordField) :
        RegistrationScreenAction

    data class ChangeTelegramIdState(val telegramId: String) : RegistrationScreenAction
    data class ChangePasswordVisibility(val passwordField: PasswordField) : RegistrationScreenAction
    data object NavigateToLoginScreen : RegistrationScreenAction
}