package ru.ari.auth.common.api.domain.models

data class Token(
    val accessToken: String,
    val tokenType: String
)