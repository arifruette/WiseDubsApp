package ru.ari.sharingpostdetails.presentation.contract

sealed interface SharingPostDetailsUiEffect {
    data class ShowMessage(val message: String) : SharingPostDetailsUiEffect
    data class CloseWithError(val message: String) : SharingPostDetailsUiEffect
    data class OpenTelegram(val telegramId: String) : SharingPostDetailsUiEffect
}
