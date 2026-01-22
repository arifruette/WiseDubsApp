package ru.ari.network.di

import dagger.BindsInstance
import dagger.Component
import ru.ari.cache.di.CacheApi
import javax.inject.Singleton

@Singleton
@Component(
    modules = [NetworkModule::class],
    dependencies = [CacheApi::class]
)
interface NetworkComponent : NetworkApi {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance @BaseUrl baseUrl: String,
            cacheApi: CacheApi
        ): NetworkComponent

    }
}