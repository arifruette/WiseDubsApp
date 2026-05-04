@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.domain.models

import kotlin.time.Instant

data class CreateBookingParams(
    val roomId: Int,
    val timeStart: Instant,
    val timeEnd: Instant,
    val duration: Int,
    val description: String?
)
