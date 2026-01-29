package ru.ari.auth.common.api.domain.models.params

data class UserRegisterParams(
    val email: String,
    val telegramId: String,
    val password: String
)