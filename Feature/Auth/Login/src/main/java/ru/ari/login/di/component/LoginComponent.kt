package ru.ari.login.di.component

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.auth.common.api.di.AuthCommonApi
import ru.ari.cache.di.CacheApi
import ru.ari.login.di.scope.LoginScreenScope
import ru.ari.login.di.modules.LoginBindsModule
import ru.ari.network.di.NetworkApi

@LoginScreenScope
@Component(
    modules = [
        LoginBindsModule::class
    ],
    dependencies = [AuthCommonApi::class]
)
interface LoginComponent {

    val loginViewModelFactory: ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            authCommonApi: AuthCommonApi
        ): LoginComponent
    }
}