package ru.ari.profile.presentation.contract

sealed interface ProfileScreenAction {
    data object Load : ProfileScreenAction
    data object RetryProfile : ProfileScreenAction
    data object ClickReservedPosts : ProfileScreenAction
    data object ClickLogout : ProfileScreenAction
    data object DismissLogout : ProfileScreenAction
    data object ConfirmLogout : ProfileScreenAction
}
