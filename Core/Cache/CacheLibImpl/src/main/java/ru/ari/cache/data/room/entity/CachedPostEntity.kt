package ru.ari.cache.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_posts")
data class CachedPostEntity(
    @PrimaryKey
    val cacheKey: String,
    val postId: Long,
    val scope: String,
    val title: String,
    val description: String,
    
    // Pickup Location fields
    val locationId: Int,
    val locationUserId: Int,
    val locationCorpus: String?,
    val locationEntrance: String?,
    val locationFloor: String?,
    val locationRoom: String,
    val locationComment: String?,
    val locationDisplayText: String?,
    val locationCreatedAt: String,
    val locationUpdatedAt: String,

    val isActive: Boolean,
    val isReserved: Boolean,
    val exchange: String,
    val reservedBy: String,
    val authorEmail: String?,
    val messageId: String,
    val createdAt: String
)
