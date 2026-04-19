package ru.ari.managepost.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ManagePostMode {

    @Immutable
    data object Create : ManagePostMode

    @Immutable
    data class Edit(val postId: Long) : ManagePostMode
}
