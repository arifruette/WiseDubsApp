package ru.ari.managepost.data.mappers

import ru.ari.managepost.data.remote.dto.PickupLocationResponse
import ru.ari.posts.api.domain.models.PickupLocation

fun PickupLocationResponse.toDomain(): PickupLocation = PickupLocation(
    id = id,
    userId = userId,
    corpus = corpus,
    entrance = entrance,
    floor = floor,
    room = room,
    comment = comment,
    displayText = displayText,
    createdAt = createdAt,
    updatedAt = updatedAt
)
