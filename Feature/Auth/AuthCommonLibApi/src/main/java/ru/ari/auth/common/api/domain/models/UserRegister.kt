package ru.ari.auth.common.api.domain.models

data class UserRegister(
    val id: Long,
    val email: String,
    val telegramId: String
)