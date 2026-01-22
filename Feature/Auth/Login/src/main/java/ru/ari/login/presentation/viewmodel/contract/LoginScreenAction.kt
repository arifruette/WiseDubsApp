package ru.ari.login.presentation.viewmodel.contract

import androidx.compose.runtime.Stable

@Stable
sealed interface LoginScreenAction {
    data object LoginUser : LoginScreenAction
    data class ChangeEmailState(val email: String) : LoginScreenAction
    data class ChangePasswordState(val password: String) : LoginScreenAction
    data object ChangePasswordVisibility : LoginScreenAction
}