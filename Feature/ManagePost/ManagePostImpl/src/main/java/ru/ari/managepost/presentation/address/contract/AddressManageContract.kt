package ru.ari.managepost.presentation.address.contract

import androidx.compose.runtime.Immutable

@Immutable
data class AddressManageState(
    val locationId: Int? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val corpus: String = "",
    val entrance: String = "",
    val floor: String = "",
    val room: String = "",
    val comment: String = "",
    val displayText: String = "",
    val isRoomError: Boolean = false,
    val error: String? = null
)

sealed interface AddressManageAction {
    data class Load(val id: Int?) : AddressManageAction
    data class ChangeCorpus(val value: String) : AddressManageAction
    data class ChangeEntrance(val value: String) : AddressManageAction
    data class ChangeFloor(val value: String) : AddressManageAction
    data class ChangeRoom(val value: String) : AddressManageAction
    data class ChangeComment(val value: String) : AddressManageAction
    data class ChangeDisplayText(val value: String) : AddressManageAction
    data object Save : AddressManageAction
    data object Delete : AddressManageAction
}

sealed interface AddressManageEffect {
    data object Back : AddressManageEffect
    data class ShowError(val message: String) : AddressManageEffect
}

interface AddressManageActionHandler {
    fun onAction(action: AddressManageAction)
}
