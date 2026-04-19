package ru.ari.cache.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.ari.cache.data.room.dao.PostDao
import ru.ari.cache.data.room.entity.CachedPostEntity
import ru.ari.cache.data.room.entity.CachedPostImageEntity

@Database(
    entities = [CachedPostEntity::class, CachedPostImageEntity::class],
    version = 3,
    exportSchema = false
)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}
