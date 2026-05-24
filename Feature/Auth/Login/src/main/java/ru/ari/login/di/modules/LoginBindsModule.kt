package ru.ari.login.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.ari.login.presentation.viewmodel.LoginViewModelFactory

@Module
interface LoginBindsModule {

    @Binds
    fun bindLoginViewModelProviderFactory(
        loginViewModelFactory: LoginViewModelFactory
    ): ViewModelProvider.Factory

}