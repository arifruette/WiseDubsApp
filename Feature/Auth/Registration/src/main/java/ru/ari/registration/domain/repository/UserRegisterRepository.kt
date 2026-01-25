package ru.ari.registration.domain.repository

import ru.ari.network.domain.models.Result
import ru.ari.registration.domain.models.UserRegister


interface UserRegisterRepository {
    suspend fun registerUser(userRegister: UserRegister): Result<UserRegister>
}