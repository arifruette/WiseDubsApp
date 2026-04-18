package ru.ari.managepost.presentation.contract

import androidx.compose.runtime.Immutable
import java.io.File
import ru.ari.managepost.presentation.models.ManagePostImageUiModel
import ru.ari.managepost.presentation.models.ManagePostMode

@Immutable
sealed interface ManagePostScreenAction {

    @Immutable
    data class Load(val mode: ManagePostMode) : ManagePostScreenAction

    @Immutable
    data class ChangeTitle(val value: String) : ManagePostScreenAction

    @Immutable
    data class ChangeDescription(val value: String) : ManagePostScreenAction

    @Immutable
    data class ChangeExchange(val value: String) : ManagePostScreenAction

    @Immutable
    data object RetryLoadRooms : ManagePostScreenAction

    @Immutable
    data object OpenCorpusSelector : ManagePostScreenAction

    @Immutable
    data object OpenRoomSelector : ManagePostScreenAction

    @Immutable
    data object DismissSelector : ManagePostScreenAction

    @Immutable
    data class ChangeRoomSearchQuery(val value: String) : ManagePostScreenAction

    @Immutable
    data class SelectCorpus(val corpus: String) : ManagePostScreenAction

    @Immutable
    data class SelectRoom(val room: String) : ManagePostScreenAction

    @Immutable
    data class AddImage(val uriString: String) : ManagePostScreenAction

    @Immutable
    data class RemoveImage(val key: String) : ManagePostScreenAction

    data class Submit(
        val images: List<ManagePostImageUiModel>,
        val localFilesByKey: Map<String, File>
    ) : ManagePostScreenAction
}
