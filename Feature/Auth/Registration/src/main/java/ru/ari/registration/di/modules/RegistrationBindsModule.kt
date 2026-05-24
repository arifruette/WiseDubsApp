package ru.ari.registration.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.ari.registration.presentation.viewmodel.RegistrationViewModelFactory

@Module
interface RegistrationBindsModule {

    @Binds
    fun bindRegistrationViewModelProviderFactory(
        registrationViewModelFactory: RegistrationViewModelFactory
    ): ViewModelProvider.Factory

}