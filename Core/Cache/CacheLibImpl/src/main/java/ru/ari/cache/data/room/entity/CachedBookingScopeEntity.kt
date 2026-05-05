package ru.ari.cache.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_booking_scopes")
data class CachedBookingScopeEntity(
    @PrimaryKey val scope: String
)

