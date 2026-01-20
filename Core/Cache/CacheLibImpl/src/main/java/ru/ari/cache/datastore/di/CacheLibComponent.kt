package ru.ari.cache.datastore.di

import dagger.Component
import ru.ari.cache.di.CacheDeps
import javax.inject.Singleton

@Singleton
@Component(
    modules = [CacheLibModule::class]
)
interface CacheLibComponent: CacheDeps