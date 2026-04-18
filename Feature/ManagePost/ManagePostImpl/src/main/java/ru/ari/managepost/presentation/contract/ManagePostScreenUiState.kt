package ru.ari.managepost.presentation.contract

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import ru.ari.managepost.presentation.models.ManagePostFormUiModel
import ru.ari.managepost.presentation.models.ManagePostMode
import ru.ari.managepost.presentation.models.ManagePostRoomGroupUiModel
import ru.ari.managepost.presentation.models.ManagePostRoomsLoadState
import ru.ari.managepost.presentation.models.ManagePostSelectorSheet

@Immutable
data class ManagePostScreenUiState(
    val mode: ManagePostMode = ManagePostMode.Create,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val form: ManagePostFormUiModel = ManagePostFormUiModel(),
    val roomGroups: ImmutableList<ManagePostRoomGroupUiModel> = persistentListOf(),
    val roomsLoadState: ManagePostRoomsLoadState = ManagePostRoomsLoadState.Loading,
    val activeSelectorSheet: ManagePostSelectorSheet = ManagePostSelectorSheet.None,
    val roomSearchQuery: String = ""
) {
    val availableCorpora: ImmutableList<String>
        get() = roomGroups.map(ManagePostRoomGroupUiModel::corpus).toImmutableList()

    val availableRooms: ImmutableList<String>
        get() = roomGroups
            .firstOrNull { it.corpus == form.selectedCorpus }
            ?.rooms
            ?: persistentListOf()

    val filteredAvailableRooms: ImmutableList<String>
        get() = availableRooms
            .filter { room ->
                roomSearchQuery.isBlank() || room.contains(roomSearchQuery.trim(), ignoreCase = true)
            }
            .toImmutableList()
}
