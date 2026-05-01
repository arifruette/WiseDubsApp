package ru.ari.sharingpostdetails.presentation.contract

sealed interface SharingPostDetailsUiAction {
    data class Load(
        val postId: Long,
        val autoReserve: Boolean = false
    ) : SharingPostDetailsUiAction
    data object ClickPrimaryAction : SharingPostDetailsUiAction
    data object ClickContact : SharingPostDetailsUiAction
    data class ClickImage(val index: Int) : SharingPostDetailsUiAction
    data object DismissImageViewer : SharingPostDetailsUiAction
}
