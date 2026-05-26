package ru.ari.auth.common.impl.data.repository

import retrofit2.HttpException
import ru.ari.auth.common.impl.data.api.AuthRetrofitApi
import ru.ari.auth.common.api.domain.models.Token
import ru.ari.auth.common.api.domain.models.UserLogin
import ru.ari.auth.common.api.domain.models.UserProfile
import ru.ari.auth.common.api.domain.models.UserRegister
import ru.ari.auth.common.api.domain.models.params.UserRegisterParams
import ru.ari.auth.common.api.domain.repository.AuthRepository
import ru.ari.auth.common.impl.data.mappers.toDomainTokenModel
import ru.ari.auth.common.impl.data.mappers.toDomainUserProfileModel
import ru.ari.auth.common.impl.data.mappers.toDomainUserRegisterModel
import ru.ari.auth.common.impl.data.mappers.toRequest
import ru.ari.auth.common.impl.di.scope.AuthScope
import ru.ari.network.domain.models.Result
import javax.inject.Inject

@AuthScope
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthRetrofitApi
) : AuthRepository {

    override suspend fun login(user: UserLogin): Result<Token> = try {
        Result.Success(api.loginUser(user.toRequest()).toDomainTokenModel())
    } catch (e: HttpException) {
        Result.Error(e.code(), e.loginErrorMessage())
    } catch (e: Throwable) {
        Result.Exception(e)
    }

    override suspend fun getCurrentUser(): Result<UserProfile> = try {
        Result.Success(api.getCurrentUser().toDomainUserProfileModel())
    } catch (e: HttpException) {
        Result.Error(e.code(), e.message())
    } catch (e: Throwable) {
        Result.Exception(e)
    }

    override suspend fun register(user: UserRegisterParams): Result<UserRegister> = try {
        Result.Success(api.registerUser(user.toRequest()).toDomainUserRegisterModel())
    } catch (e: HttpException) {
        Result.Error(e.code(), e.message())
    } catch (e: Throwable) {
        Result.Exception(e)
    }

    private fun HttpException.loginErrorMessage(): String =
        if (code() == UNAUTHORIZED_CODE) INVALID_CREDENTIALS_ERROR else message()

    private companion object {
        const val UNAUTHORIZED_CODE = 401
        const val INVALID_CREDENTIALS_ERROR = "Неверный логин или пароль"
    }
}
