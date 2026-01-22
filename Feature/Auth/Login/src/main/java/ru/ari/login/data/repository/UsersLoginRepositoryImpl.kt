package ru.ari.login.data.repository

import retrofit2.HttpException
import ru.ari.login.data.api.UserLoginApi
import ru.ari.login.data.mapper.toDomainTokenModel
import ru.ari.login.data.mapper.toRequestModel
import ru.ari.login.domain.models.Token
import ru.ari.login.domain.models.UserLogin
import ru.ari.login.domain.repository.UsersLoginRepository
import javax.inject.Inject
import ru.ari.network.domain.models.Result

class UsersLoginRepositoryImpl @Inject constructor(
    private val userLoginApi: UserLoginApi
): UsersLoginRepository {

    override suspend fun loginUser(user: UserLogin): Result<Token> {
        return try {
            val token = userLoginApi.loginUser(user.toRequestModel())
            Result.Success(token.toDomainTokenModel())
        } catch (e: HttpException) {
            Result.Error(e.code(), e.message())
        } catch (e: Throwable) {
            Result.Exception(e)
        }
    }
}