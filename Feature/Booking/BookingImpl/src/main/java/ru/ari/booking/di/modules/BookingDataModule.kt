package ru.ari.booking.di.modules

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.ari.booking.data.remote.BookingRemoteApi
import ru.ari.network.di.AuthRetrofit

@Module
class BookingDataModule {
    @Provides
    fun provideBookingRemoteApi(@AuthRetrofit retrofit: Retrofit): BookingRemoteApi {
        return retrofit.create(BookingRemoteApi::class.java)
    }
}
