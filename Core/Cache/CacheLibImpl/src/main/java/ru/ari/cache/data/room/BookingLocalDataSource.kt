package ru.ari.cache.data.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.ari.cache.data.room.dao.BookingDao
import ru.ari.cache.data.room.entity.CachedBookingEntity
import ru.ari.cache.domain.BookingDataSource
import ru.ari.cache.domain.models.BookingCacheScope
import ru.ari.cache.domain.models.CachedBooking
import javax.inject.Inject

class BookingLocalDataSource @Inject constructor(
    private val bookingDao: BookingDao
) : BookingDataSource {

    override suspend fun saveBookings(scope: BookingCacheScope, bookings: List<CachedBooking>) {
        bookingDao.replaceBookings(
            scope = scope.key,
            bookings = bookings.map { it.toEntity(scope.key) }
        )
    }

    override suspend fun getBookings(scope: BookingCacheScope): List<CachedBooking> =
        bookingDao.getBookingsSync(scope.key).map { it.toDomain() }

    override fun observeBookings(scope: BookingCacheScope): Flow<List<CachedBooking>> =
        bookingDao.observeBookings(scope.key).map { list -> list.map { it.toDomain() } }

    override suspend fun isScopeCached(scope: BookingCacheScope): Boolean =
        bookingDao.isScopeCached(scope.key)

    override suspend fun upsertBookingInExistingScopes(booking: CachedBooking) {
        val scopes = bookingDao.getScopesForBooking(booking.id)
        bookingDao.insertBookings(scopes.map { scope -> booking.toEntity(scope) })
    }

    override suspend fun deleteBookingFromExistingScopes(bookingId: Long) {
        bookingDao.deleteBookingById(bookingId)
    }

    override suspend fun clearBookings(scope: BookingCacheScope) {
        bookingDao.deleteBookingsByScope(scope.key)
        bookingDao.deleteScope(scope.key)
    }
}

private fun CachedBooking.toEntity(scope: String): CachedBookingEntity = CachedBookingEntity(
    cacheKey = "$scope:$id",
    bookingId = id,
    scope = scope,
    timeStart = timeStart,
    timeEnd = timeEnd,
    duration = duration,
    description = description,
    published = published,
    roomId = roomId,
    roomName = roomName,
    messageId = messageId,
    isMine = isMine,
    authorId = authorId,
    authorEmail = authorEmail,
    authorTelegramId = authorTelegramId
)

private fun CachedBookingEntity.toDomain(): CachedBooking = CachedBooking(
    id = bookingId,
    timeStart = timeStart,
    timeEnd = timeEnd,
    duration = duration,
    description = description,
    published = published,
    roomId = roomId,
    roomName = roomName,
    messageId = messageId,
    isMine = isMine,
    authorId = authorId,
    authorEmail = authorEmail,
    authorTelegramId = authorTelegramId
)
