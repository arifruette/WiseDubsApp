@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.data.repository

import javax.inject.Inject
import org.json.JSONObject
import retrofit2.HttpException
import ru.ari.booking.data.mappers.toDomain
import ru.ari.booking.data.mappers.toRequest
import ru.ari.booking.data.remote.BookingRemoteApi
import ru.ari.booking.domain.models.Booking
import ru.ari.booking.domain.models.CreateBookingParams
import ru.ari.booking.domain.models.GroupedRooms
import ru.ari.booking.domain.models.UpdateBookingParams
import ru.ari.booking.domain.repository.BookingRepository
import ru.ari.network.domain.models.Result
import kotlin.time.Instant

class BookingRepositoryImpl @Inject constructor(
    private val bookingRemoteApi: BookingRemoteApi
) : BookingRepository {
    override suspend fun getGroupedRooms(): Result<List<GroupedRooms>> = safeCall {
        bookingRemoteApi.getGroupedRooms().map { it.toDomain() }
    }

    override suspend fun getBookings(
        roomId: Int,
        timeStart: Instant,
        timeEnd: Instant
    ): Result<List<Booking>> = safeCall {
        bookingRemoteApi.getBookings(
            roomId = roomId,
            timeStart = timeStart.toString(),
            timeEnd = timeEnd.toString()
        ).map { it.toDomain() }
    }

    override suspend fun createBooking(params: CreateBookingParams): Result<Booking> = safeCall {
        bookingRemoteApi.createBooking(params.toRequest()).toDomain()
    }

    override suspend fun getMyBookings(): Result<List<Booking>> = safeCall {
        bookingRemoteApi.getMyBookings().map { it.toDomain() }
    }

    override suspend fun updateBooking(params: UpdateBookingParams): Result<Booking> = safeCall {
        bookingRemoteApi.updateBooking(
            postId = params.postId,
            request = params.toRequest()
        ).toDomain()
    }

    override suspend fun deleteBooking(postId: Long): Result<Unit> = safeCall {
        bookingRemoteApi.deleteBooking(postId)
    }

    private suspend inline fun <T> safeCall(crossinline block: suspend () -> T): Result<T> =
        try {
            Result.Success(block())
        } catch (e: HttpException) {
            e.toResultError()
        } catch (e: Throwable) {
            Result.Exception(e)
        }

    private fun HttpException.toResultError(): Result.Error {
        val detail = response()
            ?.errorBody()
            ?.string()
            ?.let { body -> parseErrorDetail(body) }
            ?.takeIf(String::isNotBlank)

        return Result.Error(
            code = code(),
            message = detail ?: defaultErrorMessage(code())
        )
    }

    private fun parseErrorDetail(body: String): String? =
        runCatching { JSONObject(body).optString("detail") }.getOrNull()

    private fun defaultErrorMessage(code: Int): String = when (code) {
        409 -> "Комната уже занята на выбранное время"
        404 -> "Комната или бронь не найдена"
        400, 422 -> "Проверьте интервал бронирования"
        else -> "Не удалось выполнить запрос"
    }
}
