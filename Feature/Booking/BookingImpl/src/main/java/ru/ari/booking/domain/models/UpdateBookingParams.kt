@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.domain.models

import kotlin.time.Instant

data class UpdateBookingParams(
    val postId: Long,
    val roomId: Int? = null,
    val timeStart: Instant? = null,
    val timeEnd: Instant? = null,
    val duration: Int? = null,
    val description: String? = null
)
