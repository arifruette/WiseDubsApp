package ru.ari.sharingpostdetails.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.ari.navigation.di.PostLoginRoutes
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.sharingpostdetails.navigation.SharingPostDetailsRouteProvider

@Module
interface SharingPostDetailsScreenProviderModule {

    @Binds
    @IntoSet
    @PostLoginRoutes
    fun bindSharingPostDetailsRouteProvider(
        provider: SharingPostDetailsRouteProvider
    ): RouteEntryProvider
}
