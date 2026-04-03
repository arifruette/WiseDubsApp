package ru.ari.posts.api.domain.models

data class Post(
    val id: Long,
    val title: String,
    val description: String,
    val exchange: String,
    val corpus: String,
    val room: String,
    val messageId: String,
    val isActive: Boolean,
    val isReserved: Boolean,
    val createdAt: String,
    val reservedBy: String,
    val authorEmail: String?,
    val images: List<PostImage>
)
