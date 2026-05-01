package ru.ari.auth.common.api.domain.repository

import ru.ari.auth.common.api.domain.models.Token
import ru.ari.auth.common.api.domain.models.UserLogin
import ru.ari.auth.common.api.domain.models.UserProfile
import ru.ari.auth.common.api.domain.models.UserRegister
import ru.ari.auth.common.api.domain.models.params.UserRegisterParams
import ru.ari.network.domain.models.Result
interface AuthRepository {
    suspend fun login(user: UserLogin): Result<Token>
    suspend fun getCurrentUser(): Result<UserProfile>
    suspend fun register(user: UserRegisterParams): Result<UserRegister>
}
