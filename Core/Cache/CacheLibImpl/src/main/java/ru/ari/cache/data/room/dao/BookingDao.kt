package ru.ari.cache.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.ari.cache.data.room.entity.CachedBookingEntity
import ru.ari.cache.data.room.entity.CachedBookingScopeEntity

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<CachedBookingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScope(scope: CachedBookingScopeEntity)

    @Query("SELECT * FROM cached_bookings WHERE scope = :scope ORDER BY timeStart ASC, bookingId ASC")
    fun observeBookings(scope: String): Flow<List<CachedBookingEntity>>

    @Query("SELECT * FROM cached_bookings WHERE scope = :scope ORDER BY timeStart ASC, bookingId ASC")
    suspend fun getBookingsSync(scope: String): List<CachedBookingEntity>

    @Query("SELECT DISTINCT scope FROM cached_bookings WHERE bookingId = :bookingId")
    suspend fun getScopesForBooking(bookingId: Long): List<String>

    @Query("SELECT EXISTS(SELECT 1 FROM cached_booking_scopes WHERE scope = :scope)")
    suspend fun isScopeCached(scope: String): Boolean

    @Query("DELETE FROM cached_bookings WHERE scope = :scope")
    suspend fun deleteBookingsByScope(scope: String)

    @Query("DELETE FROM cached_booking_scopes WHERE scope = :scope")
    suspend fun deleteScope(scope: String)

    @Query("DELETE FROM cached_bookings WHERE bookingId = :bookingId")
    suspend fun deleteBookingById(bookingId: Long)

    @Transaction
    suspend fun replaceBookings(scope: String, bookings: List<CachedBookingEntity>) {
        insertScope(CachedBookingScopeEntity(scope))
        deleteBookingsByScope(scope)
        insertBookings(bookings)
    }
}
