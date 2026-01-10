package ru.ari.cache.datastore.di

import dagger.Component
import ru.ari.cache.di.DataStoreDeps
import javax.inject.Singleton

@Singleton
@Component(
    modules = [DataStoreHelperModule::class]
)
interface DataStoreHelperComponent: DataStoreDeps