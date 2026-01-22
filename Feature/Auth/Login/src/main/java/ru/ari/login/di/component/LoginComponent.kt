package ru.ari.login.di.component

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.cache.di.CacheApi
import ru.ari.login.di.scope.LoginScreenScope
import ru.ari.login.di.modules.LoginBindsModule
import ru.ari.login.di.modules.LoginModule
import ru.ari.network.di.NetworkApi

@LoginScreenScope
@Component(
    modules = [
        LoginBindsModule::class,
        LoginModule::class
    ],
    dependencies = [CacheApi::class, NetworkApi::class]
)
interface LoginComponent {

    val loginViewModelFactory: ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            cacheApi: CacheApi,
            networkApi: NetworkApi
        ): LoginComponent
    }
}