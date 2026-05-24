package ru.ari.cache.data.room.entity

import androidx.room.Entity

@Entity(
    tableName = "cached_post_images",
    primaryKeys = ["postCacheKey", "imageId"]
)
data class CachedPostImageEntity(
    val postCacheKey: String,
    val imageId: Long,
    val url: String
)
