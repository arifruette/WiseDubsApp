@file:OptIn(kotlin.time.ExperimentalTime::class)

package ru.ari.booking.data.repository

import java.time.ZoneId
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import retrofit2.HttpException
import ru.ari.booking.data.mappers.toDomain
import ru.ari.booking.data.mappers.toCacheModel
import ru.ari.booking.data.mappers.toRequest
import ru.ari.booking.data.remote.BookingRemoteApi
import ru.ari.booking.domain.models.Booking
import ru.ari.booking.domain.models.CreateBookingParams
import ru.ari.booking.domain.models.GroupedRooms
import ru.ari.booking.domain.models.MyBookingsPeriod
import ru.ari.booking.domain.models.UpdateBookingParams
import ru.ari.booking.domain.repository.BookingRepository
import ru.ari.cache.domain.BookingDataSource
import ru.ari.cache.domain.models.BookingCacheScope
import ru.ari.network.domain.models.Result
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.toJavaInstant

class BookingRepositoryImpl @Inject constructor(
    private val bookingRemoteApi: BookingRemoteApi,
    private val bookingDataSource: BookingDataSource
) : BookingRepository {
    override suspend fun getGroupedRooms(): Result<List<GroupedRooms>> = safeCall {
        bookingRemoteApi.getGroupedRooms().map { it.toDomain() }
    }

    override suspend fun getBookings(
        roomId: Int,
        date: String,
        timeStart: Instant,
        timeEnd: Instant,
        forceRefresh: Boolean
    ): Result<List<Booking>> = safeCall {
        val bookings = bookingRemoteApi.getBookings(
            roomId = roomId,
            timeStart = timeStart.toString(),
            timeEnd = timeEnd.toString()
        ).map { it.toDomain() }
            .sortedBy { it.timeStart }
        bookingDataSource.saveBookings(
            scope = BookingCacheScope.DayOverview(roomId, date),
            bookings = bookings.map { it.toCacheModel() }
        )
        bookings
    }

    override suspend fun getBookingIntersections(
        roomId: Int,
        timeStart: Instant,
        timeEnd: Instant
    ): Result<List<Booking>> = safeCall {
        bookingRemoteApi.getBookings(
            roomId = roomId,
            timeStart = timeStart.toString(),
            timeEnd = timeEnd.toString()
        ).map { it.toDomain() }
            .sortedBy { it.timeStart }
    }

    override fun observeBookings(roomId: Int, date: String): Flow<List<Booking>> =
        bookingDataSource.observeBookings(BookingCacheScope.DayOverview(roomId, date)).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun createBooking(params: CreateBookingParams): Result<Booking> = safeCall {
        bookingRemoteApi.createBooking(params.toRequest()).toDomain().also { booking ->
            upsertMutationScopes(booking)
        }
    }

    override suspend fun getMyBookings(period: MyBookingsPeriod, forceRefresh: Boolean): Result<List<Booking>> = safeCall {
        val bookings = bookingRemoteApi.getMyBookings(period.apiValue).map { it.toDomain() }
        bookingDataSource.saveBookings(period.toCacheScope(), bookings.map { it.toCacheModel() })
        bookings
    }

    override fun observeMyBookings(period: MyBookingsPeriod): Flow<List<Booking>> =
        bookingDataSource.observeBookings(period.toCacheScope()).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getBookingById(postId: Long): Result<Booking> = safeCall {
        bookingRemoteApi.getBookingById(postId).toDomain()
    }

    override suspend fun updateBooking(params: UpdateBookingParams): Result<Booking> = safeCall {
        bookingRemoteApi.updateBooking(
            postId = params.postId,
            request = params.toRequest()
        ).toDomain().also { booking ->
            bookingDataSource.deleteBookingFromExistingScopes(booking.id)
            upsertMutationScopes(booking)
        }
    }

    override suspend fun deleteBooking(postId: Long): Result<Unit> = safeCall {
        val response = bookingRemoteApi.deleteBooking(postId)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        bookingDataSource.deleteBookingFromExistingScopes(postId)
    }

    private suspend fun upsertMutationScopes(booking: Booking) {
        booking.intersectedLocalDateKeys().forEach { date ->
            upsertLoadedScope(BookingCacheScope.DayOverview(booking.roomId, date), booking)
        }
        upsertLoadedScope(booking.toCurrentPeriodScope(), booking)
    }

    private suspend fun upsertLoadedScope(scope: BookingCacheScope, booking: Booking) {
        if (!bookingDataSource.isScopeCached(scope)) return
        val cachedBookings = bookingDataSource.getBookings(scope)
        val cacheModel = booking.toCacheModel()
        val updatedBookings = if (cachedBookings.any { it.id == booking.id }) {
            cachedBookings.map { if (it.id == booking.id) cacheModel else it }
        } else {
            listOf(cacheModel) + cachedBookings
        }
        bookingDataSource.saveBookings(scope, updatedBookings)
    }

    private fun Booking.toCurrentPeriodScope(): BookingCacheScope =
        if (timeEnd >= Clock.System.now()) {
            BookingCacheScope.MyUpcoming
        } else {
            BookingCacheScope.MyPast
        }

    private fun Booking.intersectedLocalDateKeys(): List<String> {
        val startDate = timeStart.toJavaInstant().atZone(UI_ZONE).toLocalDate()
        val endDateTime = timeEnd.toJavaInstant().atZone(UI_ZONE).toLocalDateTime()
        val inclusiveEndDate = if (endDateTime.toLocalTime() == LocalTime.MIDNIGHT) {
            endDateTime.toLocalDate().minusDays(1)
        } else {
            endDateTime.toLocalDate()
        }
        val dates = mutableListOf<String>()
        var current = startDate
        val lastDate = inclusiveEndDate.coerceAtLeast(startDate)
        while (!current.isAfter(lastDate)) {
            dates += current.format(DATE_FORMATTER)
            current = current.plusDays(1)
        }
        return dates
    }

    private fun LocalDate.coerceAtLeast(minimum: LocalDate): LocalDate =
        if (isBefore(minimum)) minimum else this

    private fun MyBookingsPeriod.toCacheScope(): BookingCacheScope = when (this) {
        MyBookingsPeriod.Upcoming -> BookingCacheScope.MyUpcoming
        MyBookingsPeriod.Past -> BookingCacheScope.MyPast
    }

    private suspend inline fun <T> safeCall(crossinline block: suspend () -> T): Result<T> =
        try {
            Result.Success(block())
        } catch (e: CancellationException) {
            throw e
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

    private companion object {
        val UI_ZONE: ZoneId = ZoneId.systemDefault()
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}
