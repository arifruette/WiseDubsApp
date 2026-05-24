package ru.ari.sharingpostdetails.presentation.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class SharingPostDetailsUiModel(
    val id: Long,
    val title: String,
    val description: String,
    val exchange: String,
    val authorEmail: String?,
    val authorTelegramId: String?,
    val createdAt: String,
    val address: String,
    val addressComment: String?,
    val status: ReservationStatusUi,
    val primaryAction: PrimaryActionUiModel,
    val images: ImmutableList<PostDetailsImageUiModel>
)

@Immutable
data class PrimaryActionUiModel(
    val type: PrimaryActionType,
    val label: String,
    val isEnabled: Boolean,
    val isLoading: Boolean
)

@Immutable
data class PostDetailsImageUiModel(
    val id: Long,
    val url: String
)

@Immutable
enum class ReservationStatusUi {
    AVAILABLE,
    RESERVED_BY_ME,
    RESERVED_BY_OTHER
}

@Immutable
enum class PrimaryActionType {
    RESERVE,
    UNRESERVE,
    NONE
}
