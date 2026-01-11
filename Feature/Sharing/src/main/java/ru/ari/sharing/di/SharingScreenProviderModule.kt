package ru.ari.sharing.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.ari.navigation.di.PostLoginRoutes
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.sharing.navigation.SharingScreenRouteProvider

@Module
interface SharingScreenProviderModule {

    @Binds
    @IntoSet
    @PostLoginRoutes
    fun bindSharingScreenProvider(sharingScreenProvider: SharingScreenRouteProvider): RouteEntryProvider
}