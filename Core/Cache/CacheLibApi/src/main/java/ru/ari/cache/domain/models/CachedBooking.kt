package ru.ari.cache.domain.models

data class CachedBooking(
    val id: Long,
    val timeStart: String,
    val timeEnd: String,
    val duration: Int,
    val description: String?,
    val published: Boolean,
    val roomId: Int,
    val roomName: String,
    val messageId: String?,
    val isMine: Boolean,
    val authorId: Long,
    val authorEmail: String,
    val authorTelegramId: String
)
