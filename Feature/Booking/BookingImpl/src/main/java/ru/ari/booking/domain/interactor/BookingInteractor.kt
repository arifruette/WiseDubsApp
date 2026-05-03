@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.domain.interactor

import ru.ari.booking.domain.models.Booking
import ru.ari.booking.domain.models.CreateBookingParams
import ru.ari.booking.domain.models.GroupedRooms
import ru.ari.booking.domain.models.UpdateBookingParams
import ru.ari.network.domain.models.Result
import kotlin.time.Instant

interface BookingInteractor {
    suspend fun getGroupedRooms(): Result<List<GroupedRooms>>

    suspend fun getBookings(
        roomId: Int,
        timeStart: Instant,
        timeEnd: Instant
    ): Result<List<Booking>>

    suspend fun createBooking(params: CreateBookingParams): Result<Booking>

    suspend fun getMyBookings(): Result<List<Booking>>

    suspend fun updateBooking(params: UpdateBookingParams): Result<Booking>

    suspend fun deleteBooking(postId: Long): Result<Unit>
}
