package ru.ari.login.presentation.viewmodel.contract

sealed interface LoginScreenAction {
    data class LoginUser(val onSuccess: () -> Unit) : LoginScreenAction
    data class ChangeEmailState(val email: String) : LoginScreenAction
    data class ChangePasswordState(val password: String) : LoginScreenAction
    data object ChangePasswordVisibility : LoginScreenAction
}