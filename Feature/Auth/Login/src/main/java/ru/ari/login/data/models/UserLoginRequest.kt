package ru.ari.login.data.models

data class UserLoginRequest(
    val email: String,
    val password: String,
)