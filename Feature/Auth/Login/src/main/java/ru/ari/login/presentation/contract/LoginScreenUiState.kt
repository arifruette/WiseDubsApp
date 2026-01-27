package ru.ari.login.presentation.contract

import androidx.compose.runtime.Immutable

@Immutable
data class LoginScreenUiState(
    val isLoading: Boolean = false,
    val isPasswordTextVisible: Boolean = false,
    val emailText: String = "",
    val passwordText: String = "",
)