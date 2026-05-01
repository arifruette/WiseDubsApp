package ru.ari.profile.presentation.contract

sealed interface ReservedPostsScreenUiEffect {
    data object NavigateBack : ReservedPostsScreenUiEffect
    data object OpenSharing : ReservedPostsScreenUiEffect
    data class OpenPostDetails(val postId: Long) : ReservedPostsScreenUiEffect
}
