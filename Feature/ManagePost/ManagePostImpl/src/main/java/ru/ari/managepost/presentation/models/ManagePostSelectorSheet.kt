package ru.ari.managepost.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ManagePostSelectorSheet {

    @Immutable
    data object None : ManagePostSelectorSheet

    @Immutable
    data object Corpus : ManagePostSelectorSheet

    @Immutable
    data object Room : ManagePostSelectorSheet
}
