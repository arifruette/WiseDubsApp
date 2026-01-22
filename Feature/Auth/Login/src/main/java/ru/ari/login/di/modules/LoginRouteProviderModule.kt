package ru.ari.login.di.modules

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.ari.login.presentation.navigation.LoginScreenProvider
import ru.ari.navigation.di.PreLoginRoutes
import ru.ari.navigation.di.RouteEntryProvider

@Module
interface LoginRouteProviderModule {

    @Binds
    @IntoSet
    @PreLoginRoutes
    fun bindLoginRouteProvider(loginScreenProvider: LoginScreenProvider): RouteEntryProvider
}