package ru.ari.posts.api.domain.models

import java.io.File

data class UpdatePostParams(
    val postId: Long,
    val title: String,
    val description: String?,
    val exchange: String?,
    val pickupLocationId: Int?,
    val messageId: String,
    val reservedBy: String,
    val imageFiles: List<File>?
)
