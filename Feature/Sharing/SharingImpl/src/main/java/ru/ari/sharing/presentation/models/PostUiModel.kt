package ru.ari.sharing.presentation.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class PostUiModel(
    val id: Long,
    val title: String,
    val exchange: String,
    val authorEmail: String?,
    val reservationStatus: PostReservationStatusUi,
    val images: ImmutableList<PostImageUiModel>
)

@Immutable
enum class PostReservationStatusUi {
    AVAILABLE,
    RESERVED_BY_ME,
    RESERVED_BY_OTHER
}

@Immutable
data class PostImageUiModel(
    val id: Long,
    val url: String
)
