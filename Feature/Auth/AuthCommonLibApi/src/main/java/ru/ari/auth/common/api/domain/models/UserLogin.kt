package ru.ari.auth.common.api.domain.models

data class UserLogin(
    val email: String,
    val password: String
)