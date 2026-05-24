package ru.ari.booking.di.component

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.booking.di.modules.BookingBindsModule
import ru.ari.booking.di.modules.BookingDataModule
import ru.ari.booking.di.scope.BookingScreenScope
import ru.ari.cache.di.CacheApi
import ru.ari.network.di.NetworkApi

@BookingScreenScope
@Component(
    modules = [
        BookingBindsModule::class,
        BookingDataModule::class
    ],
    dependencies = [
        NetworkApi::class,
        CacheApi::class
    ]
)
interface BookingComponent {
    val bookingViewModelFactory: ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            networkApi: NetworkApi,
            cacheApi: CacheApi
        ): BookingComponent
    }
}
