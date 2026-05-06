@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.domain.repository

import ru.ari.booking.domain.models.Booking
import ru.ari.booking.domain.models.CreateBookingParams
import ru.ari.booking.domain.models.GroupedRooms
import ru.ari.booking.domain.models.MyBookingsPeriod
import ru.ari.booking.domain.models.UpdateBookingParams
import ru.ari.network.domain.models.Result
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface BookingRepository {
    suspend fun getGroupedRooms(): Result<List<GroupedRooms>>

    suspend fun getBookings(
        roomId: Int,
        date: String,
        timeStart: Instant,
        timeEnd: Instant,
        forceRefresh: Boolean
    ): Result<List<Booking>>

    suspend fun getBookingIntersections(
        roomId: Int,
        timeStart: Instant,
        timeEnd: Instant
    ): Result<List<Booking>>

    fun observeBookings(roomId: Int, date: String): Flow<List<Booking>>

    suspend fun createBooking(params: CreateBookingParams): Result<Booking>

    suspend fun getMyBookings(period: MyBookingsPeriod, forceRefresh: Boolean): Result<List<Booking>>

    fun observeMyBookings(period: MyBookingsPeriod): Flow<List<Booking>>

    suspend fun getBookingById(postId: Long): Result<Booking>

    suspend fun updateBooking(params: UpdateBookingParams): Result<Booking>

    suspend fun deleteBooking(postId: Long): Result<Unit>
}
