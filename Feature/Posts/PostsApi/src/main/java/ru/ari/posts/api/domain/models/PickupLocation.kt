package ru.ari.posts.api.domain.models

data class PickupLocation(
    val id: Int,
    val userId: Int,
    val corpus: String?,
    val entrance: String?,
    val floor: String?,
    val room: String,
    val comment: String?,
    val label: String?,
    val createdAt: String,
    val updatedAt: String
)

data class CreatePickupLocationParams(
    val corpus: String?,
    val entrance: String?,
    val floor: String?,
    val room: String,
    val comment: String?,
    val label: String?
)

data class UpdatePickupLocationParams(
    val corpus: String?,
    val entrance: String?,
    val floor: String?,
    val room: String?,
    val comment: String?,
    val label: String?
)
