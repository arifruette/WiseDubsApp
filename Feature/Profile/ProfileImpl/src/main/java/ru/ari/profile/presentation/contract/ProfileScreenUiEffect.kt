package ru.ari.profile.presentation.contract

sealed interface ProfileScreenUiEffect {
    data object OpenReservedPosts : ProfileScreenUiEffect
    data object OpenMyBookings : ProfileScreenUiEffect
    data class ShowError(val message: String) : ProfileScreenUiEffect
}
