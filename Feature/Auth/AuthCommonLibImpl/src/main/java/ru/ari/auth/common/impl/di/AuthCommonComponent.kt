package ru.ari.auth.common.impl.di

import dagger.Component
import ru.ari.auth.common.api.di.AuthCommonApi
import ru.ari.auth.common.impl.di.module.AuthCommonBindsModule
import ru.ari.auth.common.impl.di.module.AuthCommonModule
import ru.ari.auth.common.impl.di.scope.AuthScope
import ru.ari.cache.di.CacheApi
import ru.ari.network.di.NetworkApi

@AuthScope
@Component(
    modules = [
        AuthCommonModule::class,
        AuthCommonBindsModule::class
    ],
    dependencies = [NetworkApi::class, CacheApi::class]
)
interface AuthCommonComponent : AuthCommonApi {

    @Component.Factory
    interface Factory {
        fun create(networkApi: NetworkApi, cacheApi: CacheApi): AuthCommonComponent
    }

}