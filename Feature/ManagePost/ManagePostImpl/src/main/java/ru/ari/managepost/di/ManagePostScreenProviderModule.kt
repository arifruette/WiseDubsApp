package ru.ari.managepost.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.ari.managepost.navigation.ManagePostScreenRouteProvider
import ru.ari.navigation.di.PostLoginRoutes
import ru.ari.navigation.di.RouteEntryProvider

@Module
interface ManagePostScreenProviderModule {

    @Binds
    @IntoSet
    @PostLoginRoutes
    fun bindManagePostScreenProvider(
        provider: ManagePostScreenRouteProvider
    ): RouteEntryProvider
}
