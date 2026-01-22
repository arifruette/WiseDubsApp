package ru.ari.login.domain.repository

import ru.ari.login.domain.models.Token
import ru.ari.login.domain.models.UserLogin
import ru.ari.network.domain.models.Result

interface UsersLoginRepository {
    suspend fun loginUser(user: UserLogin): Result<Token>
}