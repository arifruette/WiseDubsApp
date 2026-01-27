package ru.ari.auth.common.api.di

import ru.ari.auth.common.api.domain.interactor.AuthInteractor
import ru.ari.auth.common.api.domain.repository.AuthRepository

interface AuthCommonApi {
    val authInteractor: AuthInteractor
    val authRepository: AuthRepository
}