package ru.ari.cache.domain.models

data class CachedPickupLocation(
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

data class CachedPost(
    val id: Long,
    val title: String,
    val description: String,
    val pickupLocation: CachedPickupLocation,
    val isActive: Boolean,
    val isReserved: Boolean,
    val exchange: String,
    val reservedBy: String,
    val reservedById: Long? = null,
    val authorEmail: String?,
    val authorTelegramId: String? = null,
    val messageId: String,
    val createdAt: String,
    val images: List<CachedPostImage>
)
