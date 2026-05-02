package ru.ari.profile.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.ari.navigation.di.PostLoginRoutes
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.profile.navigation.ProfileScreenRouteProvider

@Module
interface ProfileScreenProviderModule {

    @Binds
    @IntoSet
    @PostLoginRoutes
    fun bindProfileScreenProvider(
        profileScreenRouteProvider: ProfileScreenRouteProvider
    ): RouteEntryProvider
}
