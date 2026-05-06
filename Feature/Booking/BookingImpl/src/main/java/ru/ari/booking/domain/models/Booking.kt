@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.domain.models

import kotlin.time.Instant

data class BookingAuthor(
    val id: Long,
    val email: String,
    val telegramId: String
)

data class Booking(
    val id: Long,
    val timeStart: Instant,
    val timeEnd: Instant,
    val duration: Int,
    val description: String?,
    val published: Boolean,
    val roomId: Int,
    val roomName: String,
    val messageId: String?,
    val isMine: Boolean,
    val author: BookingAuthor
)
