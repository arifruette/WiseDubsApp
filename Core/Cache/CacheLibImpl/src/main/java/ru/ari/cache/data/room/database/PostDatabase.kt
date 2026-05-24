package ru.ari.cache.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.ari.cache.data.room.dao.PostDao
import ru.ari.cache.data.room.dao.BookingDao
import ru.ari.cache.data.room.entity.CachedBookingEntity
import ru.ari.cache.data.room.entity.CachedBookingScopeEntity
import ru.ari.cache.data.room.entity.CachedPostEntity
import ru.ari.cache.data.room.entity.CachedPostImageEntity

@Database(
    entities = [
        CachedPostEntity::class,
        CachedPostImageEntity::class,
        CachedBookingEntity::class,
        CachedBookingScopeEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun bookingDao(): BookingDao
}
