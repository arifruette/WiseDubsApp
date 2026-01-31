package ru.ari.login.presentation.contract

import androidx.compose.runtime.Stable

@Stable
sealed interface LoginScreenAction {
    data object LoginUser : LoginScreenAction
    data class ChangeEmailState(val email: String) : LoginScreenAction
    data class ChangePasswordState(val password: String) : LoginScreenAction
    data object ChangePasswordVisibility : LoginScreenAction
    data object NavigateToRegistrationScreen : LoginScreenAction
}