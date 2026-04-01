package ru.ari.sharing.data.models

import com.google.gson.annotations.SerializedName

data class PostResponse(
    val id: Long,
    val title: String,
    val description: String,
    val exchange: String,
    val corpus: String,
    val room: String,
    @SerializedName("message_id")
    val messageId: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_reserved")
    val isReserved: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("reserved_by")
    val reservedBy: String,
    @SerializedName("author_email")
    val authorEmail: String?,
    val images: List<ImageResponse>
)
