package ru.ari.login.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.ari.login.data.repository.UsersLoginRepositoryImpl
import ru.ari.login.di.scope.LoginScreenScope
import ru.ari.login.domain.repository.UsersLoginRepository
import ru.ari.login.presentation.viewmodel.LoginViewModelFactory

@Module
interface LoginBindsModule {

    @LoginScreenScope
    @Binds
    fun bindUserLoginRepositoryImpl(
        usersLoginRepositoryImpl: UsersLoginRepositoryImpl
    ): UsersLoginRepository

    @Binds
    fun bindLoginViewModelProviderFactory(
        loginViewModelFactory: LoginViewModelFactory
    ): ViewModelProvider.Factory
}