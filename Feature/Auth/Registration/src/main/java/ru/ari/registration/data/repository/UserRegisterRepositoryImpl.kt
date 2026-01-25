package ru.ari.registration.data.repository

import retrofit2.HttpException
import ru.ari.registration.data.api.UserRegisterApi
import ru.ari.registration.domain.models.UserRegister
import ru.ari.registration.domain.repository.UserRegisterRepository
import javax.inject.Inject
import ru.ari.network.domain.models.Result
import ru.ari.registration.data.mapper.toDomainUserRegisterModel

class UserRegisterRepositoryImpl @Inject constructor(
    private val userRegisterApi: UserRegisterApi
): UserRegisterRepository {
    override suspend fun registerUser(userRegister: UserRegister): Result<UserRegister> {
        return try {
            val userResult = userRegisterApi.registerUser(userRegister)
            Result.Success(userResult.toDomainUserRegisterModel())
        } catch (e: HttpException) {
            Result.Error(e.code(), e.message())
        } catch (e: Throwable) {
            Result.Exception(e)
        }
    }
}