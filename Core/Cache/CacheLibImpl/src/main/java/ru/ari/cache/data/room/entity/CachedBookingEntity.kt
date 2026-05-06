package ru.ari.cache.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_bookings")
data class CachedBookingEntity(
    @PrimaryKey val cacheKey: String,
    val bookingId: Long,
    val scope: String,
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
