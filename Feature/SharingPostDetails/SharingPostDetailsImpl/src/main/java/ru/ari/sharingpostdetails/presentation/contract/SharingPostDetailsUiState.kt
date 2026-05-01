package ru.ari.sharingpostdetails.presentation.contract

import androidx.compose.runtime.Immutable
import ru.ari.sharingpostdetails.presentation.models.SharingPostDetailsUiModel

@Immutable
sealed interface SharingPostDetailsUiState {

    @Immutable
    data object Loading : SharingPostDetailsUiState

    @Immutable
    data class Content(
        val post: SharingPostDetailsUiModel,
        val selectedImageIndex: Int? = null,
        val isPrimaryActionInProgress: Boolean = false
    ) : SharingPostDetailsUiState
}
