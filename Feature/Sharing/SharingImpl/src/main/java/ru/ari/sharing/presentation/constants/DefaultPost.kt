package ru.ari.sharing.presentation.constants

import ru.ari.posts.api.domain.models.Post

val DefaultPost = Post(
    id = -1L,
    title = "Загрузка...",
    description = "",
    exchange = "",
    corpus = "",
    room = "",
    messageId = "",
    isActive = true,
    isReserved = false,
    createdAt = "",
    reservedBy = "",
    authorEmail = "",
    images = emptyList()
)
