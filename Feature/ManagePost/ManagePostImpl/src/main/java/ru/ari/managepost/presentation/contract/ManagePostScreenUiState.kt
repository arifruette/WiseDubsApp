package ru.ari.managepost.presentation.contract

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.ari.managepost.presentation.models.ManagePostFormUiModel
import ru.ari.managepost.presentation.models.ManagePostMode
import ru.ari.managepost.presentation.models.ManagePostRoomsLoadState
import ru.ari.managepost.presentation.models.ManagePostSelectorSheet
import ru.ari.posts.api.domain.models.PickupLocation

@Immutable
data class ManagePostScreenUiState(
    val mode: ManagePostMode = ManagePostMode.Create,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val form: ManagePostFormUiModel = ManagePostFormUiModel(),
    val pickupLocations: ImmutableList<PickupLocation> = persistentListOf(),
    val locationsLoadState: ManagePostRoomsLoadState = ManagePostRoomsLoadState.Loading,
    val activeSelectorSheet: ManagePostSelectorSheet = ManagePostSelectorSheet.None
)
