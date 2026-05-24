package ru.ari.managepost.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ManagePostImageUiModel {
    val key: String
    val previewUrl: String

    @Immutable
    data class Remote(
        override val key: String,
        override val previewUrl: String
    ) : ManagePostImageUiModel

    @Immutable
    data class Local(
        override val key: String,
        override val previewUrl: String
    ) : ManagePostImageUiModel
}
