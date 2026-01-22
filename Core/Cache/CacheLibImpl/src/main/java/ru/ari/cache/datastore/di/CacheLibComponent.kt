package ru.ari.cache.datastore.di

import dagger.Component
import ru.ari.cache.di.CacheApi
import javax.inject.Singleton

@Singleton
@Component(
    modules = [CacheLibModule::class]
)
interface CacheLibComponent: CacheApi