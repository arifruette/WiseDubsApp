package ru.ari.cache.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [CacheLibModule::class]
)
interface CacheLibComponent : CacheApi {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): CacheLibComponent
    }
}