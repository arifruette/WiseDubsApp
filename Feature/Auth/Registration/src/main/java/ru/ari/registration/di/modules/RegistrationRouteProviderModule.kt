package ru.ari.registration.di.modules

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.ari.navigation.di.PreLoginRoutes
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.registration.presentation.navigation.RegistrationScreenProvider

@Module
interface RegistrationRouteProviderModule {

    @Binds
    @IntoSet
    @PreLoginRoutes
    fun bindRegistrationRouteProvider(registrationRouteEntryProvider: RegistrationScreenProvider): RouteEntryProvider
}