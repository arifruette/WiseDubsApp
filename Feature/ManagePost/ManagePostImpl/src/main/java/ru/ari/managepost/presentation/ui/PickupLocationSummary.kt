package ru.ari.managepost.presentation.ui

import ru.ari.posts.api.domain.models.PickupLocation

internal fun buildPickupLocationSummary(
    corpus: String,
    entrance: String,
    floor: String,
    room: String
): String = listOfNotNull(
    corpus.trim().takeIf(String::isNotEmpty)?.let { "$it корпус" },
    entrance.trim().takeIf(String::isNotEmpty)?.let { "$it подъезд" },
    floor.trim().takeIf(String::isNotEmpty)?.let { "$it этаж" },
    room.trim().takeIf(String::isNotEmpty)?.let { "$it комната"}
).joinToString(", ")

internal fun PickupLocation.buildSummary(): String = buildPickupLocationSummary(
    corpus = corpus.orEmpty(),
    entrance = entrance.orEmpty(),
    floor = floor.orEmpty(),
    room = room
)
