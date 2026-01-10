package ru.ari.cache.datastore.di

import dagger.Binds
import dagger.Module
import ru.ari.cache.datastore.DataStoreHelper
import ru.ari.cache.datastore.MockDataStoreHelperImpl
import javax.inject.Singleton

@Module
interface DataStoreHelperModule {

    @Binds
    @Singleton
    fun bindDataStoreHelper(mockDataStoreHelperImpl: MockDataStoreHelperImpl): DataStoreHelper
}