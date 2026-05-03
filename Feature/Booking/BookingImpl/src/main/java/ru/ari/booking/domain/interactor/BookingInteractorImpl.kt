@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.domain.interactor

import javax.inject.Inject
import ru.ari.booking.domain.models.Booking
import ru.ari.booking.domain.models.CreateBookingParams
import ru.ari.booking.domain.models.GroupedRooms
import ru.ari.booking.domain.models.UpdateBookingParams
import ru.ari.booking.domain.repository.BookingRepository
import ru.ari.network.domain.models.Result
import kotlin.time.Instant

class BookingInteractorImpl @Inject constructor(
    private val bookingRepository: BookingRepository
) : BookingInteractor {
    override suspend fun getGroupedRooms(): Result<List<GroupedRooms>> =
        bookingRepository.getGroupedRooms()

    override suspend fun getBookings(
        roomId: Int,
        timeStart: Instant,
        timeEnd: Instant
    ): Result<List<Booking>> = bookingRepository.getBookings(
        roomId = roomId,
        timeStart = timeStart,
        timeEnd = timeEnd
    )

    override suspend fun createBooking(params: CreateBookingParams): Result<Booking> =
        bookingRepository.createBooking(params)

    override suspend fun getMyBookings(): Result<List<Booking>> =
        bookingRepository.getMyBookings()

    override suspend fun updateBooking(params: UpdateBookingParams): Result<Booking> =
        bookingRepository.updateBooking(params)

    override suspend fun deleteBooking(postId: Long): Result<Unit> =
        bookingRepository.deleteBooking(postId)
}
