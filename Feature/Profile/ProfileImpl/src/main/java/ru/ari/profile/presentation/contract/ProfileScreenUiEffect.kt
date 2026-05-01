package ru.ari.profile.presentation.contract

sealed interface ProfileScreenUiEffect {
    data object NavigateToLogin : ProfileScreenUiEffect
    data object OpenReservedPosts : ProfileScreenUiEffect
    data class ShowError(val message: String) : ProfileScreenUiEffect
}
