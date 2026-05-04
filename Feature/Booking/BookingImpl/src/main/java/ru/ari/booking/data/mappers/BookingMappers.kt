@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.data.mappers

import ru.ari.booking.data.remote.dto.BookingResponse
import ru.ari.booking.data.remote.dto.CreateBookingRequest
import ru.ari.booking.data.remote.dto.GroupedRoomsResponse
import ru.ari.booking.data.remote.dto.RoomResponse
import ru.ari.booking.data.remote.dto.UpdateBookingRequest
import ru.ari.booking.domain.models.Booking
import ru.ari.booking.domain.models.CreateBookingParams
import ru.ari.booking.domain.models.GroupedRooms
import ru.ari.booking.domain.models.Room
import ru.ari.booking.domain.models.UpdateBookingParams
import kotlin.time.Instant

fun RoomResponse.toDomain(): Room = Room(
    id = id,
    roomName = roomName,
    corpus = corpus
)

fun GroupedRoomsResponse.toDomain(): GroupedRooms = GroupedRooms(
    corpus = corpus,
    rooms = rooms.map { it.toDomain() }
)

fun BookingResponse.toDomain(): Booking = Booking(
    id = id,
    timeStart = Instant.parse(timeStart),
    timeEnd = Instant.parse(timeEnd),
    duration = duration,
    description = description,
    telegramId = telegramId,
    published = published,
    roomId = roomId,
    roomName = roomName,
    messageId = messageId
)

fun CreateBookingParams.toRequest(): CreateBookingRequest = CreateBookingRequest(
    roomId = roomId,
    timeStart = timeStart.toString(),
    timeEnd = timeEnd.toString(),
    duration = duration,
    description = description
)

fun UpdateBookingParams.toRequest(): UpdateBookingRequest = UpdateBookingRequest(
    roomId = roomId,
    timeStart = timeStart?.toString(),
    timeEnd = timeEnd?.toString(),
    duration = duration,
    description = description
)
