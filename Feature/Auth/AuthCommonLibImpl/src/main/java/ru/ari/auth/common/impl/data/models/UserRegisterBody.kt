package ru.ari.auth.common.impl.data.models

data class UserRegisterBody(
    val email: String,
    val telegramId: String,
    val password: String
)