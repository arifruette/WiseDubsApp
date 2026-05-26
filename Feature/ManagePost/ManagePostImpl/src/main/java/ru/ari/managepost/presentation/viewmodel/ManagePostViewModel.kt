package ru.ari.managepost.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.managepost.domain.repository.PickupLocationRepository
import ru.ari.managepost.presentation.contract.ManagePostScreenAction
import ru.ari.managepost.presentation.contract.ManagePostScreenUiEffect
import ru.ari.managepost.presentation.contract.ManagePostScreenUiState
import ru.ari.managepost.presentation.models.ManagePostFormUiModel
import ru.ari.managepost.presentation.models.ManagePostImageUiModel
import ru.ari.managepost.presentation.models.ManagePostMode
import ru.ari.managepost.presentation.models.ManagePostRoomsLoadState
import ru.ari.managepost.presentation.models.ManagePostSelectorSheet
import ru.ari.network.domain.models.Result
import ru.ari.network.domain.models.toUserErrorMessage
import ru.ari.posts.api.domain.interactor.PostsInteractor
import ru.ari.posts.api.domain.models.CreatePostParams
import ru.ari.posts.api.domain.models.PickupLocation
import ru.ari.posts.api.domain.models.UpdatePostParams

class ManagePostViewModel @Inject constructor(
    private val postsInteractor: PostsInteractor,
    private val pickupLocationRepository: PickupLocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagePostScreenUiState())
    val uiState: StateFlow<ManagePostScreenUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ManagePostScreenUiEffect>()
    val uiEffect: SharedFlow<ManagePostScreenUiEffect> = _uiEffect.asSharedFlow()

    private var loadedMode: ManagePostMode? = null
    private var hasLoadedInitialData = false
    private var pendingAddressRefresh: PendingAddressRefresh? = null
    private var localImageCounter = 0L

    fun prepareForMode(mode: ManagePostMode) {
        if (hasLoadedInitialData && loadedMode == mode) {
            return
        }

        _uiState.value = ManagePostScreenUiState(
            mode = mode,
            isLoading = true,
            locationsLoadState = ManagePostRoomsLoadState.Loading
        )
    }

    fun onAction(action: ManagePostScreenAction) {
        when (action) {
            is ManagePostScreenAction.Load -> {
                if (!hasLoadedInitialData || loadedMode != action.mode) {
                    loadInitial(action.mode)
                } else {
                    refreshLocations(action.mode)
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

            ManagePostScreenAction.RetryLoadLocations -> {
                _uiState.update {
                    it.copy(
                        locationsLoadState = ManagePostRoomsLoadState.Loading,
                        activeSelectorSheet = ManagePostSelectorSheet.None
                    )
                }
                refreshLocations(_uiState.value.mode)
            }

            ManagePostScreenAction.OpenAddressSelector -> {
                if (_uiState.value.locationsLoadState is ManagePostRoomsLoadState.Content) {
                    _uiState.update { it.copy(activeSelectorSheet = ManagePostSelectorSheet.Address) }
                }
            }

            ManagePostScreenAction.DismissSelector -> {
                _uiState.update { it.copy(activeSelectorSheet = ManagePostSelectorSheet.None) }
            }

            is ManagePostScreenAction.SelectAddress -> {
                updateForm { copy(selectedAddress = action.address) }
                _uiState.update { it.copy(activeSelectorSheet = ManagePostSelectorSheet.None) }
            }

            is ManagePostScreenAction.EditAddress -> {
                pendingAddressRefresh = PendingAddressRefresh.Edit(action.id)
                _uiState.update { it.copy(activeSelectorSheet = ManagePostSelectorSheet.None) }
                emitEffect(ManagePostScreenUiEffect.NavigateToAddressManage(action.id))
            }

            ManagePostScreenAction.CreateAddress -> {
                pendingAddressRefresh = PendingAddressRefresh.Create(
                    knownIds = _uiState.value.pickupLocations.mapTo(mutableSetOf(), PickupLocation::id)
                )
                _uiState.update { it.copy(activeSelectorSheet = ManagePostSelectorSheet.None) }
                emitEffect(ManagePostScreenUiEffect.NavigateToAddressManage(null))
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

    private fun loadInitial(mode: ManagePostMode) {
        hasLoadedInitialData = true
        loadedMode = mode
        pendingAddressRefresh = null
        _uiState.value = ManagePostScreenUiState(
            mode = mode,
            isLoading = true,
            locationsLoadState = ManagePostRoomsLoadState.Loading
        )
        loadLocations(mode = mode, loadPostAfterLocations = mode is ManagePostMode.Edit)
    }

    private fun refreshLocations(mode: ManagePostMode) {
        loadLocations(mode = mode, loadPostAfterLocations = false)
    }

    private fun loadLocations(
        mode: ManagePostMode,
        loadPostAfterLocations: Boolean
    ) {
        viewModelScope.launch {
            when (val locationsResult = pickupLocationRepository.getMyPickupLocations()) {
                is Result.Success -> {
                    val locations = locationsResult.data.toImmutableList()
                    _uiState.update {
                        it.copy(
                            pickupLocations = locations,
                            locationsLoadState = ManagePostRoomsLoadState.Content
                        )
                    }

                    if (loadPostAfterLocations && mode is ManagePostMode.Edit) {
                        loadEditPost(mode, locations)
                    } else {
                        applyAddressSelection(locations, mode)
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }

                is Result.Error -> handleLocationsLoadingError(locationsResult.message)
                is Result.Exception -> {
                    handleLocationsLoadingError(
                        locationsResult.error.toUserErrorMessage("Не удалось загрузить список адресов")
                    )
                }
            }
        }
    }

    private suspend fun loadEditPost(
        mode: ManagePostMode.Edit,
        locations: List<PickupLocation>
    ) {
        when (val postResult = postsInteractor.getPostById(mode.postId)) {
            is Result.Success -> {
                val post = postResult.data
                val selectedAddress = locations.find { it.id == post.pickupLocation.id } ?: post.pickupLocation

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        form = ManagePostFormUiModel(
                            title = post.title,
                            description = post.description,
                            exchange = post.exchange,
                            selectedAddress = selectedAddress,
                            reservedBy = post.reservedBy,
                            messageId = post.messageId,
                            images = post.images.map { image ->
                                ManagePostImageUiModel.Remote(
                                    key = "remote-${image.id}",
                                    previewUrl = image.url,
                                    id = image.id
                                )
                            }.toImmutableList()
                        )
                    )
                }
            }

            is Result.Error -> emitError(postResult.message)
            is Result.Exception -> emitError(postResult.error.toUserErrorMessage("Не удалось загрузить пост"))
        }
    }

    private fun applyAddressSelection(
        locations: List<PickupLocation>,
        mode: ManagePostMode
    ) {
        val currentSelectedId = _uiState.value.form.selectedAddress?.id
        val resolvedAddress = when (val refresh = pendingAddressRefresh) {
            is PendingAddressRefresh.Create -> {
                locations.firstOrNull { it.id !in refresh.knownIds }
                    ?: currentSelectedId?.let { selectedId ->
                        locations.find { it.id == selectedId }
                    }
                    ?: locations.firstOrNull()
            }

            is PendingAddressRefresh.Edit -> {
                locations.find { it.id == refresh.id } ?: locations.firstOrNull()
            }

            null -> {
                currentSelectedId?.let { selectedId ->
                    locations.find { it.id == selectedId } ?: locations.firstOrNull()
                } ?: when (mode) {
                    ManagePostMode.Create -> locations.firstOrNull()
                    is ManagePostMode.Edit -> null
                }
            }
        }

        pendingAddressRefresh = null
        updateForm { copy(selectedAddress = resolvedAddress) }
    }

    private fun submit(
        images: List<ManagePostImageUiModel>,
        localFilesByKey: Map<String, File>
    ) {
        val state = _uiState.value
        val form = state.form
        val isCreate = state.mode is ManagePostMode.Create

        if (form.title.isBlank()) {
            emitError("Введите заголовок")
            return
        }
        if (form.selectedAddress == null) {
            emitError("Выберите адрес")
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            var preparedFiles = emptyList<File>()
            try {
                val result = when (val mode = state.mode) {
                    ManagePostMode.Create -> {
                        preparedFiles = prepareLocalUploadFiles(images, localFilesByKey)
                        postsInteractor.createPost(
                            CreatePostParams(
                                title = form.title.trim(),
                                description = form.description.trim().ifBlank { null },
                                exchange = form.exchange.trim().ifBlank { null },
                                pickupLocationId = form.selectedAddress.id,
                                messageId = "",
                                reservedBy = "",
                                imageFiles = preparedFiles
                            )
                        )
                    }

                    is ManagePostMode.Edit -> {
                        val retainedRemoteImageIds = images
                            .filterIsInstance<ManagePostImageUiModel.Remote>()
                            .map(ManagePostImageUiModel.Remote::id)

                        postsInteractor.updatePost(
                            UpdatePostParams(
                                postId = mode.postId,
                                title = form.title.trim(),
                                description = form.description.trim().ifBlank { null },
                                exchange = form.exchange.trim().ifBlank { null },
                                pickupLocationId = form.selectedAddress.id,
                                messageId = form.messageId,
                                reservedBy = form.reservedBy,
                                imageFiles = if (form.imagesChanged) {
                                    prepareLocalUploadFiles(images, localFilesByKey).also {
                                        preparedFiles = it
                                    }
                                } else {
                                    null
                                },
                                retainedImageIds = retainedRemoteImageIds.takeIf {
                                    form.imagesChanged && it.isNotEmpty()
                                },
                                clearImages = true.takeIf {
                                    form.imagesChanged && retainedRemoteImageIds.isEmpty()
                                }
                            )
                        )
                    }
                }

                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isSaving = false) }
                        emitEffect(ManagePostScreenUiEffect.Completed(isCreate = isCreate))
                    }

                    is Result.Error -> {
                        _uiState.update { it.copy(isSaving = false) }
                        emitError(result.message)
                    }

                    is Result.Exception -> {
                        _uiState.update { it.copy(isSaving = false) }
                        emitError(result.error.toUserErrorMessage("Не удалось сохранить пост"))
                    }
                }
            } catch (error: Throwable) {
                _uiState.update { it.copy(isSaving = false) }
                emitError(error.toUserErrorMessage("Не удалось подготовить изображения"))
            } finally {
                preparedFiles.distinct().forEach(File::delete)
            }
        }
    }

    private fun prepareLocalUploadFiles(
        images: List<ManagePostImageUiModel>,
        localFilesByKey: Map<String, File>
    ): List<File> = images
        .filterIsInstance<ManagePostImageUiModel.Local>()
        .map { image ->
            localFilesByKey[image.key] ?: error("Не удалось подготовить локальное изображение")
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

    private fun handleLocationsLoadingError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isSaving = false,
                locationsLoadState = ManagePostRoomsLoadState.Error(message),
                activeSelectorSheet = ManagePostSelectorSheet.None
            )
        }
    }

    private fun emitEffect(effect: ManagePostScreenUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }

    private sealed interface PendingAddressRefresh {
        data class Create(val knownIds: Set<Int>) : PendingAddressRefresh
        data class Edit(val id: Int) : PendingAddressRefresh
    }
}
