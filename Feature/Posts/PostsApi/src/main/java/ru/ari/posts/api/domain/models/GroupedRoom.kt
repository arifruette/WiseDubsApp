package ru.ari.posts.api.domain.models

data class GroupedRoom(
    val corpus: String,
    val rooms: List<Room>
)
