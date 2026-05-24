package ru.ari.auth.common.api.domain.models

data class UserProfile(
    val id: Long,
    val email: String,
    val telegramId: String
)
