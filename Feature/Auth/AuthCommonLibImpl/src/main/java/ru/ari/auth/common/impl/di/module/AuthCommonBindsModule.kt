package ru.ari.auth.common.impl.di.module

import dagger.Binds
import dagger.Module
import ru.ari.auth.common.api.domain.interactor.AuthInteractor
import ru.ari.auth.common.api.domain.repository.AuthRepository
import ru.ari.auth.common.impl.data.interactor.AuthInteractorImpl
import ru.ari.auth.common.impl.data.repository.AuthRepositoryImpl
import ru.ari.auth.common.impl.di.scope.AuthScope
import javax.inject.Singleton

@Module
interface AuthCommonBindsModule {

    @Binds
    @AuthScope
    fun bindAuthInteractor(authInteractorImpl: AuthInteractorImpl): AuthInteractor

    @Binds
    @AuthScope
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
}