package ru.ari.cache.domain.models

data class SessionState(
    val token: String,
    val userLogin: String
)