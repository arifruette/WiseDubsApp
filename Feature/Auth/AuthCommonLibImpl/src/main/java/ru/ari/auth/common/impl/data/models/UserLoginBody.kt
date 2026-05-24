package ru.ari.auth.common.impl.data.models

data class UserLoginBody(
    val email: String,
    val password: String,
)