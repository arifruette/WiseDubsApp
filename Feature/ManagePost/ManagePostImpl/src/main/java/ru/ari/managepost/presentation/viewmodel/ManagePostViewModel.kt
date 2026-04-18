package ru.ari.managepost.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import java.net.URL
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.managepost.presentation.contract.ManagePostScreenAction
import ru.ari.managepost.presentation.contract.ManagePostScreenUiEffect
import ru.ari.managepost.presentation.contract.ManagePostScreenUiState
import ru.ari.managepost.presentation.models.ManagePostFormUiModel
import ru.ari.managepost.presentation.models.ManagePostImageUiModel
import ru.ari.managepost.presentation.models.ManagePostMode
import ru.ari.managepost.presentation.models.ManagePostRoomGroupUiModel
import ru.ari.managepost.presentation.models.ManagePostRoomsLoadState
import ru.ari.managepost.presentation.models.ManagePostSelectorSheet
import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.interactor.PostsInteractor
import ru.ari.posts.api.domain.models.CreatePostParams
import ru.ari.posts.api.domain.models.UpdatePostParams
import javax.inject.Inject

class ManagePostViewModel @Inject constructor(
    private val postsInteractor: PostsInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagePostScreenUiState())
    val uiState: StateFlow<ManagePostScreenUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ManagePostScreenUiEffect>()
    val uiEffect: SharedFlow<ManagePostScreenUiEffect> = _uiEffect.asSharedFlow()

    private var hasLoaded = false
    private var localImageCounter = 0L

    fun onAction(action: ManagePostScreenAction) {
        when (action) {
            is ManagePostScreenAction.Load -> {
                if (!hasLoaded || _uiState.value.mode != action.mode) {
                    load(action.mode)
                }
            }

            is ManagePostScreenAction.ChangeTitle -> {
                updateForm { copy(title = action.value.take(40)) }
            }

            is ManagePostScreenAction.ChangeDescription -> {
                updateForm { copy(description = action.value.take(200)) }
            }

            is ManagePostScreenAction.ChangeExchange -> {
                updateForm { copy(exchange = action.value) }
            }

            ManagePostScreenAction.RetryLoadRooms -> load(_uiState.value.mode)

            ManagePostScreenAction.OpenCorpusSelector -> {
                if (_uiState.value.roomsLoadState is ManagePostRoomsLoadState.Content) {
                    _uiState.update {
                        it.copy(
                            activeSelectorSheet = ManagePostSelectorSheet.Corpus,
                            roomSearchQuery = ""
                        )
                    }
                }
            }

            ManagePostScreenAction.OpenRoomSelector -> {
                val state = _uiState.value
                if (
                    state.roomsLoadState is ManagePostRoomsLoadState.Content &&
                    state.form.selectedCorpus.isNotBlank()
                ) {
                    _uiState.update {
                        it.copy(
                            activeSelectorSheet = ManagePostSelectorSheet.Room,
                            roomSearchQuery = ""
                        )
                    }
                }
            }

            ManagePostScreenAction.DismissSelector -> {
                _uiState.update {
                    it.copy(
                        activeSelectorSheet = ManagePostSelectorSheet.None,
                        roomSearchQuery = ""
                    )
                }
            }

            is ManagePostScreenAction.ChangeRoomSearchQuery -> {
                _uiState.update { it.copy(roomSearchQuery = action.value) }
            }

            is ManagePostScreenAction.SelectCorpus -> {
                val availableRooms = _uiState.value.roomGroups
                    .firstOrNull { it.corpus == action.corpus }
                    ?.rooms
                    ?: persistentListOf()
                val currentRoom = _uiState.value.form.selectedRoom
                updateForm {
                    copy(
                        selectedCorpus = action.corpus,
                        selectedRoom = currentRoom.takeIf { it in availableRooms }.orEmpty()
                    )
                }
                _uiState.update {
                    it.copy(
                        activeSelectorSheet = ManagePostSelectorSheet.Room,
                        roomSearchQuery = ""
                    )
                }
            }

            is ManagePostScreenAction.SelectRoom -> {
                updateForm { copy(selectedRoom = action.room) }
                _uiState.update {
                    it.copy(
                        activeSelectorSheet = ManagePostSelectorSheet.None,
                        roomSearchQuery = ""
                    )
                }
            }

            is ManagePostScreenAction.AddImage -> {
                localImageCounter += 1
                updateForm {
                    copy(
                        images = (images + ManagePostImageUiModel.Local(
                            key = "local-$localImageCounter",
                            previewUrl = action.uriString
                        )).toImmutableList(),
                        imagesChanged = true
                    )
                }
            }

            is ManagePostScreenAction.RemoveImage -> {
                updateForm {
                    copy(
                        images = images.filterNot { it.key == action.key }.toImmutableList(),
                        imagesChanged = true
                    )
                }
            }

            is ManagePostScreenAction.Submit -> submit(
                images = action.images,
                localFilesByKey = action.localFilesByKey
            )
        }
    }

    private fun load(mode: ManagePostMode) {
        hasLoaded = true
        _uiState.update {
            it.copy(
                mode = mode,
                isLoading = mode is ManagePostMode.Edit,
                isSaving = false,
                roomsLoadState = ManagePostRoomsLoadState.Loading,
                activeSelectorSheet = ManagePostSelectorSheet.None,
                roomSearchQuery = ""
            )
        }

        viewModelScope.launch {
            when (val roomsResult = postsInteractor.getGroupedRooms()) {
                is Result.Success -> {
                    val roomGroups = roomsResult.data
                        .map { group ->
                            ManagePostRoomGroupUiModel(
                                corpus = group.corpus,
                                rooms = group.rooms
                                    .map { room -> room.roomName }
                                    .distinct()
                                    .toImmutableList()
                            )
                        }
                        .toImmutableList()

                    when (mode) {
                        ManagePostMode.Create -> applyCreateState(roomGroups)
                        is ManagePostMode.Edit -> loadEditPost(mode, roomGroups)
                    }
                }

                is Result.Error -> handleRoomsLoadingError(roomsResult.message)
                is Result.Exception -> {
                    handleRoomsLoadingError(
                        roomsResult.error.message ?: "Не удалось загрузить список комнат"
                    )
                }
            }
        }
    }

    private fun applyCreateState(roomGroups: ImmutableList<ManagePostRoomGroupUiModel>) {
        _uiState.value = ManagePostScreenUiState(
            mode = ManagePostMode.Create,
            isLoading = false,
            roomGroups = roomGroups,
            roomsLoadState = ManagePostRoomsLoadState.Content,
            form = ManagePostFormUiModel(
                selectedCorpus = "",
                selectedRoom = ""
            )
        )
    }

    private suspend fun loadEditPost(
        mode: ManagePostMode.Edit,
        roomGroups: ImmutableList<ManagePostRoomGroupUiModel>
    ) {
        when (val postResult = postsInteractor.getPostById(mode.postId)) {
            is Result.Success -> {
                val post = postResult.data
                val selectedCorpus = roomGroups.firstOrNull { it.corpus == post.corpus }?.corpus
                    ?: post.corpus
                val selectedRoom = roomGroups
                    .firstOrNull { it.corpus == selectedCorpus }
                    ?.rooms
                    ?.firstOrNull { it == post.room }
                    ?: post.room

                _uiState.value = ManagePostScreenUiState(
                    mode = mode,
                    isLoading = false,
                    roomGroups = roomGroups,
                    roomsLoadState = ManagePostRoomsLoadState.Content,
                    form = ManagePostFormUiModel(
                        title = post.title,
                        description = post.description,
                        exchange = post.exchange,
                        selectedCorpus = selectedCorpus,
                        selectedRoom = selectedRoom,
                        reservedBy = post.reservedBy,
                        messageId = post.messageId,
                        images = post.images.map { image ->
                            ManagePostImageUiModel.Remote(
                                key = "remote-${image.id}",
                                previewUrl = image.url
                            )
                        }.toImmutableList()
                    )
                )
            }

            is Result.Error -> emitError(postResult.message)
            is Result.Exception -> emitError(postResult.error.message ?: "Не удалось загрузить пост")
        }
    }

    private fun submit(
        images: List<ManagePostImageUiModel>,
        localFilesByKey: Map<String, File>
    ) {
        val state = _uiState.value
        val form = state.form

        if (form.title.isBlank()) {
            emitError("Введите заголовок")
            return
        }
        if (form.selectedCorpus.isBlank()) {
            emitError("Выберите корпус")
            return
        }
        if (form.selectedRoom.isBlank()) {
            emitError("Выберите комнату")
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            var preparedFiles = emptyList<File>()
            try {
                val result = when (val mode = state.mode) {
                    ManagePostMode.Create -> {
                        preparedFiles = prepareUploadFiles(images, localFilesByKey)
                        postsInteractor.createPost(
                            CreatePostParams(
                                title = form.title.trim(),
                                description = form.description.trim().ifBlank { null },
                                exchange = form.exchange.trim().ifBlank { null },
                                corpus = form.selectedCorpus,
                                room = form.selectedRoom,
                                messageId = "",
                                reservedBy = "",
                                imageFiles = preparedFiles
                            )
                        )
                    }

                    is ManagePostMode.Edit -> postsInteractor.updatePost(
                        UpdatePostParams(
                            postId = mode.postId,
                            title = form.title.trim(),
                            description = form.description.trim().ifBlank { null },
                            exchange = form.exchange.trim().ifBlank { null },
                            corpus = form.selectedCorpus,
                            room = form.selectedRoom,
                            messageId = form.messageId,
                            reservedBy = form.reservedBy,
                            imageFiles = if (form.imagesChanged) {
                                prepareUploadFiles(images, localFilesByKey).also {
                                    preparedFiles = it
                                }
                            } else {
                                null
                            }
                        )
                    )
                }

                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isSaving = false) }
                        emitEffect(ManagePostScreenUiEffect.Completed)
                    }

                    is Result.Error -> {
                        _uiState.update { it.copy(isSaving = false) }
                        emitError(result.message)
                    }

                    is Result.Exception -> {
                        _uiState.update { it.copy(isSaving = false) }
                        emitError(result.error.message ?: "Не удалось сохранить пост")
                    }
                }
            } catch (error: Throwable) {
                _uiState.update { it.copy(isSaving = false) }
                emitError(error.message ?: "Не удалось подготовить изображения")
            } finally {
                preparedFiles.distinct().forEach(File::delete)
            }
        }
    }

    private fun prepareUploadFiles(
        images: List<ManagePostImageUiModel>,
        localFilesByKey: Map<String, File>
    ): List<File> = images.mapIndexed { index, image ->
        when (image) {
            is ManagePostImageUiModel.Local -> {
                localFilesByKey[image.key]
                    ?: error("Не удалось подготовить локальное изображение")
            }

            is ManagePostImageUiModel.Remote -> downloadRemoteImage(url = image.previewUrl, index = index)
        }
    }

    private fun downloadRemoteImage(url: String, index: Int): File {
        val tempFile = File.createTempFile("manage_post_remote_$index", ".jpg")
        URL(url).openStream().use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    private fun updateForm(transform: ManagePostFormUiModel.() -> ManagePostFormUiModel) {
        _uiState.update { state -> state.copy(form = state.form.transform()) }
    }

    private fun emitError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isSaving = false,
                activeSelectorSheet = ManagePostSelectorSheet.None
            )
        }
        emitEffect(ManagePostScreenUiEffect.ShowError(message))
    }

    private fun handleRoomsLoadingError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isSaving = false,
                roomsLoadState = ManagePostRoomsLoadState.Error(message),
                activeSelectorSheet = ManagePostSelectorSheet.None,
                roomSearchQuery = ""
            )
        }
    }

    private fun emitEffect(effect: ManagePostScreenUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }
}
