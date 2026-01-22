package ru.ari.login.domain.models

data class Token(
    val accessToken: String,
    val tokenType: String
)