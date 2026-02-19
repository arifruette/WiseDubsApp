package ru.ari.cache.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.ari.cache.domain.datastore.DataStoreHelper
import ru.ari.cache.data.datastore.MockDataStoreHelperImpl
import ru.ari.cache.data.room.SharingPostLocalDataSource
import ru.ari.cache.data.room.dao.SharingPostDao
import ru.ari.cache.data.room.database.SharingDatabase
import ru.ari.cache.domain.SharingPostDataSource
import javax.inject.Singleton

@Module
interface CacheLibModule {

    @Binds
    @Singleton
    fun bindDataStoreHelper(mockDataStoreHelperImpl: MockDataStoreHelperImpl): DataStoreHelper

    @Binds
    @Singleton
    fun bindSharingPostDataSource(
        sharingPostLocalDataSource: SharingPostLocalDataSource
    ): SharingPostDataSource

    companion object {
        @Provides
        @Singleton
        fun provideSharingDatabase(context: Context): SharingDatabase =
            Room.databaseBuilder(context, SharingDatabase::class.java, "sharing_posts.db")
                .fallbackToDestructiveMigration(true)
                .build()

        @Provides
        fun provideSharingPostDao(database: SharingDatabase): SharingPostDao =
            database.sharingPostDao()
    }
}
