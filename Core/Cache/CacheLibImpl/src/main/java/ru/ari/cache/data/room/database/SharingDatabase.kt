package ru.ari.cache.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.ari.cache.data.room.dao.SharingPostDao
import ru.ari.cache.data.room.entity.SharingPostEntity
import ru.ari.cache.data.room.entity.SharingPostImageEntity

@Database(
    entities = [SharingPostEntity::class, SharingPostImageEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SharingDatabase : RoomDatabase() {
    abstract fun sharingPostDao(): SharingPostDao
}
