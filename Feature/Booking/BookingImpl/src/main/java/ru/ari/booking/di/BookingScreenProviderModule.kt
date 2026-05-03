package ru.ari.booking.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.ari.booking.navigation.BookingScreenRouteProvider
import ru.ari.navigation.di.PostLoginRoutes
import ru.ari.navigation.di.RouteEntryProvider

@Module
interface BookingScreenProviderModule {
    @Binds
    @IntoSet
    @PostLoginRoutes
    fun bindBookingScreenProvider(provider: BookingScreenRouteProvider): RouteEntryProvider
}
