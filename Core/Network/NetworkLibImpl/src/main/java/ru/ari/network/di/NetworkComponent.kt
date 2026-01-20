package ru.ari.network.di

import dagger.BindsInstance
import dagger.Component
import ru.ari.cache.di.CacheDeps
import javax.inject.Singleton

@Singleton
@Component(
    modules = [NetworkModule::class],
    dependencies = [CacheDeps::class]
)
interface NetworkComponent : NetworkDeps {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance @BaseUrl baseUrl: String,
            cacheDeps: CacheDeps
        ): NetworkComponent

    }
}