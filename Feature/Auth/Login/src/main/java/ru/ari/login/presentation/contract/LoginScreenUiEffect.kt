package ru.ari.login.presentation.contract

sealed interface LoginScreenUiEffect {
    data class ShowError(val message: String) : LoginScreenUiEffect

    data object NavigateToMainScreen: LoginScreenUiEffect
}