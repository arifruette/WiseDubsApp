package ru.ari.posts.data.models

data class GroupedRoomsResponse(
    val corpus: String,
    val rooms: List<RoomResponse>
)
