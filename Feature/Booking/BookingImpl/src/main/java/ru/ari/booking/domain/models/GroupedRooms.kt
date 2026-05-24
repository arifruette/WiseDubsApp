package ru.ari.booking.domain.models

data class GroupedRooms(
    val corpus: String,
    val rooms: List<Room>
)
