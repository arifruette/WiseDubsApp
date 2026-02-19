package ru.ari.cache.data.room.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.ari.cache.data.room.entity.SharingPostEntity
import ru.ari.cache.data.room.entity.SharingPostImageEntity

data class SharingPostWithImages(
    @Embedded
    val post: SharingPostEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val images: List<SharingPostImageEntity>
)
