package ru.ari.registration.presentation.contract

sealed interface RegistrationScreenUiEffect {
    data class ShowError(val message: String) : RegistrationScreenUiEffect
    data object NavigateToMainScreen : RegistrationScreenUiEffect
    data object NavigateToLoginScreen: RegistrationScreenUiEffect
}