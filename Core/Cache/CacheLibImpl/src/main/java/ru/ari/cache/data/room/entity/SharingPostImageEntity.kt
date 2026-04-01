package ru.ari.cache.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_post_images",
    foreignKeys = [
        ForeignKey(
            entity = SharingPostEntity::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["postId"])]
)
data class SharingPostImageEntity(
    @PrimaryKey
    val id: Long,
    val postId: Long,
    val url: String
)
