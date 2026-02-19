package ru.ari.sharing.presentation.contract

import androidx.compose.runtime.Stable

@Stable
sealed interface SharingScreenUiEffect {
    data class ShowError(val message: String) : SharingScreenUiEffect
    data class NavigateToDetails(val postId: Long) : SharingScreenUiEffect
}
