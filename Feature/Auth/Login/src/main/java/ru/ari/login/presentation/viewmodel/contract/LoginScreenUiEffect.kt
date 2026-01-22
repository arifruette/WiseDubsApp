package ru.ari.login.presentation.viewmodel.contract

sealed interface LoginScreenUiEffect {
    data class ShowError(val message: String) : LoginScreenUiEffect
}