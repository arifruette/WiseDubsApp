package ru.ari.cache.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.ari.cache.domain.datastore.DataStoreHelper
import ru.ari.cache.data.datastore.SessionDataStoreHelperImpl
import ru.ari.cache.data.room.BookingLocalDataSource
import ru.ari.cache.data.room.PostLocalDataSource
import ru.ari.cache.data.room.dao.BookingDao
import ru.ari.cache.data.room.dao.PostDao
import ru.ari.cache.data.room.database.PostDatabase
import ru.ari.cache.domain.BookingDataSource
import ru.ari.cache.domain.PostDataSource
import javax.inject.Singleton

@Module
interface CacheLibModule {

    @Binds
    @Singleton
    fun bindDataStoreHelper(sessionDataStoreHelperImpl: SessionDataStoreHelperImpl): DataStoreHelper

    @Binds
    @Singleton
    fun bindPostDataSource(
        postLocalDataSource: PostLocalDataSource
    ): PostDataSource

    @Binds
    @Singleton
    fun bindBookingDataSource(
        bookingLocalDataSource: BookingLocalDataSource
    ): BookingDataSource

    companion object {
        @Provides
        @Singleton
        fun providePostDatabase(context: Context): PostDatabase =
            Room.databaseBuilder(context, PostDatabase::class.java, "posts.db")
                .fallbackToDestructiveMigration(true)
                .build()

        @Provides
        fun providePostDao(database: PostDatabase): PostDao =
            database.postDao()

        @Provides
        fun provideBookingDao(database: PostDatabase): BookingDao =
            database.bookingDao()
    }
}
