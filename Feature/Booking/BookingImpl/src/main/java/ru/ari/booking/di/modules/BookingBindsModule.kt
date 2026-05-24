package ru.ari.booking.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.ari.booking.data.repository.BookingRepositoryImpl
import ru.ari.booking.domain.interactor.BookingInteractor
import ru.ari.booking.domain.interactor.BookingInteractorImpl
import ru.ari.booking.domain.repository.BookingRepository
import ru.ari.booking.presentation.viewmodel.BookingViewModelFactory

@Module
interface BookingBindsModule {
    @Binds
    fun bindBookingRepository(impl: BookingRepositoryImpl): BookingRepository

    @Binds
    fun bindBookingInteractor(impl: BookingInteractorImpl): BookingInteractor

    @Binds
    fun bindBookingViewModelFactory(factory: BookingViewModelFactory): ViewModelProvider.Factory
}
