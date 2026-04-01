package ru.ari.cache.domain.models

data class SharingPost(
    val id: Long,
    val title: String,
    val description: String,
    val corpus: String,
    val room: String,
    val isActive: Boolean,
    val isReserved: Boolean,
    val exchange: String,
    val reservedBy: String,
    val authorEmail: String?,
    val messageId: String,
    val createdAt: String,
    val images: List<SharingPostImage>
)
