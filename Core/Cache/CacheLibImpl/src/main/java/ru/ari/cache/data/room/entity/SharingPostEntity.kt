package ru.ari.cache.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_posts")
data class SharingPostEntity(
    @PrimaryKey
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
    val createdAt: String
)
