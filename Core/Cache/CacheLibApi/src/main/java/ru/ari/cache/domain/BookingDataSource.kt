package ru.ari.cache.domain

import kotlinx.coroutines.flow.Flow
import ru.ari.cache.domain.models.BookingCacheScope
import ru.ari.cache.domain.models.CachedBooking

interface BookingDataSource {
    suspend fun saveBookings(scope: BookingCacheScope, bookings: List<CachedBooking>)
    suspend fun getBookings(scope: BookingCacheScope): List<CachedBooking>
    fun observeBookings(scope: BookingCacheScope): Flow<List<CachedBooking>>
    suspend fun isScopeCached(scope: BookingCacheScope): Boolean
    suspend fun upsertBookingInExistingScopes(booking: CachedBooking)
    suspend fun deleteBookingFromExistingScopes(bookingId: Long)
    suspend fun clearBookings(scope: BookingCacheScope)
}
