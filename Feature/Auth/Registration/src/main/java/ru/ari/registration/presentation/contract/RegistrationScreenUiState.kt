package ru.ari.registration.presentation.contract

data class RegistrationScreenUiState(
    val emailText: String = "",
    val telegramIdText: String = "",
    val isFirstPasswordVisible: Boolean = false,
    val isSecondPasswordVisible: Boolean = false,
    val firstPasswordText: String = "",
    val secondPasswordText: String = "",
    val isLoading: Boolean = false,
)