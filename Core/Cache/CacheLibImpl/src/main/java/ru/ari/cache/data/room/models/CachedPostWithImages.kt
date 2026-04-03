package ru.ari.cache.data.room.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.ari.cache.data.room.entity.CachedPostEntity
import ru.ari.cache.data.room.entity.CachedPostImageEntity

data class CachedPostWithImages(
    @Embedded
    val post: CachedPostEntity,
    @Relation(
        parentColumn = "cacheKey",
        entityColumn = "postCacheKey"
    )
    val images: List<CachedPostImageEntity>
)
