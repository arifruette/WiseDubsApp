package ru.ari.managepost.presentation.contract

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ManagePostScreenUiEffect {

    @Immutable
    data class ShowError(val message: String) : ManagePostScreenUiEffect

    @Immutable
    data object Completed : ManagePostScreenUiEffect

    @Immutable
    data class NavigateToAddressManage(val locationId: Int? = null) : ManagePostScreenUiEffect
}
