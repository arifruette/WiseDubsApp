package ru.ari.auth.common.impl.data.models

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: Long,
    val email: String,
    @SerializedName("telegram_id")
    val telegramId: String
)
