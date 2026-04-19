package ru.ari.managepost.presentation.address.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ari.managepost.domain.repository.PickupLocationRepository
import ru.ari.managepost.presentation.address.contract.AddressManageAction
import ru.ari.managepost.presentation.address.contract.AddressManageActionHandler
import ru.ari.managepost.presentation.address.contract.AddressManageEffect
import ru.ari.managepost.presentation.address.contract.AddressManageState
import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.models.CreatePickupLocationParams
import ru.ari.posts.api.domain.models.UpdatePickupLocationParams

class AddressManageViewModel @Inject constructor(
    private val repository: PickupLocationRepository
) : ViewModel(), AddressManageActionHandler {

    private val _state = MutableStateFlow(AddressManageState())
    val state: StateFlow<AddressManageState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddressManageEffect>()
    val effect: SharedFlow<AddressManageEffect> = _effect.asSharedFlow()

    fun prepareForRoute(id: Int?) {
        if (id == null) {
            _state.value = AddressManageState()
            return
        }

        val currentState = _state.value
        if (currentState.locationId == id && (currentState.isLoading || currentState.room.isNotBlank())) {
            return
        }

        _state.value = AddressManageState(
            locationId = id,
            isLoading = true
        )
    }

    override fun onAction(action: AddressManageAction) {
        when (action) {
            is AddressManageAction.Load -> load(action.id)
            is AddressManageAction.ChangeCorpus -> _state.update { it.copy(corpus = action.value.take(15)) }
            is AddressManageAction.ChangeEntrance -> _state.update { it.copy(entrance = action.value.take(15)) }
            is AddressManageAction.ChangeFloor -> _state.update { it.copy(floor = action.value.take(15)) }
            is AddressManageAction.ChangeRoom -> _state.update {
                it.copy(room = action.value.take(15), isRoomError = false)
            }

            is AddressManageAction.ChangeComment -> _state.update { it.copy(comment = action.value.take(250)) }
            is AddressManageAction.ChangeDisplayText -> _state.update {
                it.copy(displayText = action.value.take(250))
            }

            AddressManageAction.Save -> save()
            AddressManageAction.Delete -> delete()
        }
    }

    private fun load(id: Int?) {
        if (id == null) {
            _state.value = AddressManageState()
            return
        }

        _state.update { it.copy(locationId = id, isLoading = true) }
        viewModelScope.launch {
            when (val result = repository.getPickupLocationById(id)) {
                is Result.Success -> {
                    val location = result.data
                    _state.update {
                        it.copy(
                            locationId = location.id,
                            isLoading = false,
                            corpus = location.corpus.orEmpty(),
                            entrance = location.entrance.orEmpty(),
                            floor = location.floor.orEmpty(),
                            room = location.room,
                            comment = location.comment.orEmpty(),
                            displayText = location.displayText.orEmpty()
                        )
                    }
                }

                is Result.Error -> emitError(result.message)
                is Result.Exception -> emitError(result.error.message ?: "Ошибка загрузки")
            }
        }
    }

    private fun save() {
        val currentState = _state.value
        if (currentState.room.isBlank()) {
            _state.update { it.copy(isRoomError = true) }
            return
        }

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val result = if (currentState.locationId == null) {
                repository.createPickupLocation(
                    CreatePickupLocationParams(
                        corpus = currentState.corpus.ifBlank { null },
                        entrance = currentState.entrance.ifBlank { null },
                        floor = currentState.floor.ifBlank { null },
                        room = currentState.room,
                        comment = currentState.comment.ifBlank { null },
                        displayText = currentState.displayText.ifBlank { null }
                    )
                )
            } else {
                repository.updatePickupLocation(
                    currentState.locationId,
                    UpdatePickupLocationParams(
                        corpus = currentState.corpus.ifBlank { null },
                        entrance = currentState.entrance.ifBlank { null },
                        floor = currentState.floor.ifBlank { null },
                        room = currentState.room,
                        comment = currentState.comment.ifBlank { null },
                        displayText = currentState.displayText.ifBlank { null }
                    )
                )
            }

            when (result) {
                is Result.Success -> {
                    _state.update { it.copy(isSaving = false) }
                    _effect.emit(AddressManageEffect.Back)
                }

                is Result.Error -> emitError(result.message)
                is Result.Exception -> emitError(result.error.message ?: "Ошибка сохранения")
            }
        }
    }

    private fun delete() {
        val id = _state.value.locationId ?: return
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            when (val result = repository.deletePickupLocation(id)) {
                is Result.Success -> {
                    _state.update { it.copy(isSaving = false) }
                    _effect.emit(AddressManageEffect.Back)
                }

                is Result.Error -> emitError(result.message)
                is Result.Exception -> emitError(result.error.message ?: "Ошибка удаления")
            }
        }
    }

    private suspend fun emitError(message: String) {
        _state.update { it.copy(isLoading = false, isSaving = false) }
        _effect.emit(AddressManageEffect.ShowError(message))
    }
}
