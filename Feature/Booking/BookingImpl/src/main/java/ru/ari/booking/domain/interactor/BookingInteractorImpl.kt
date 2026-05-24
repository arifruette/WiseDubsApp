@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.domain.interactor

import javax.inject.Inject
import ru.ari.booking.domain.models.Booking
import ru.ari.booking.domain.models.CreateBookingParams
import ru.ari.booking.domain.models.GroupedRooms
import ru.ari.booking.domain.models.MyBookingsPeriod
import ru.ari.booking.domain.models.UpdateBookingParams
import ru.ari.booking.domain.repository.BookingRepository
import ru.ari.network.domain.models.Result
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

class BookingInteractorImpl @Inject constructor(
    private val bookingRepository: BookingRepository
) : BookingInteractor {
    override suspend fun getGroupedRooms(): Result<List<GroupedRooms>> =
        bookingRepository.getGroupedRooms()

    override suspend fun getBookings(
        roomId: Int,
        date: String,
        timeStart: Instant,
        timeEnd: Instant,
        forceRefresh: Boolean
    ): Result<List<Booking>> = bookingRepository.getBookings(
        roomId = roomId,
        date = date,
        timeStart = timeStart,
        timeEnd = timeEnd,
        forceRefresh = forceRefresh
    )

    override suspend fun getBookingIntersections(
        roomId: Int,
        timeStart: Instant,
        timeEnd: Instant
    ): Result<List<Booking>> = bookingRepository.getBookingIntersections(
        roomId = roomId,
        timeStart = timeStart,
        timeEnd = timeEnd
    )

    override fun observeBookings(roomId: Int, date: String): Flow<List<Booking>> =
        bookingRepository.observeBookings(roomId, date)

    override suspend fun createBooking(params: CreateBookingParams): Result<Booking> =
        bookingRepository.createBooking(params)

    override suspend fun getMyBookings(period: MyBookingsPeriod, forceRefresh: Boolean): Result<List<Booking>> =
        bookingRepository.getMyBookings(period, forceRefresh)

    override fun observeMyBookings(period: MyBookingsPeriod): Flow<List<Booking>> =
        bookingRepository.observeMyBookings(period)

    override suspend fun getBookingById(postId: Long): Result<Booking> =
        bookingRepository.getBookingById(postId)

    override suspend fun updateBooking(params: UpdateBookingParams): Result<Booking> =
        bookingRepository.updateBooking(params)

    override suspend fun deleteBooking(postId: Long): Result<Unit> =
        bookingRepository.deleteBooking(postId)
}
