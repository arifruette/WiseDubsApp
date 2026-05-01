package ru.ari.auth.common.impl.data.interactor

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ari.auth.common.api.domain.interactor.AuthInteractor
import ru.ari.auth.common.api.domain.models.Token
import ru.ari.auth.common.api.domain.models.UserLogin
import ru.ari.auth.common.api.domain.models.UserProfile
import ru.ari.auth.common.api.domain.models.UserRegister
import ru.ari.auth.common.api.domain.models.params.UserRegisterParams
import ru.ari.auth.common.api.domain.repository.AuthRepository
import ru.ari.cache.domain.datastore.DataStoreHelper
import ru.ari.cache.domain.models.SessionState
import ru.ari.network.domain.models.Result
import ru.ari.network.domain.models.onSuccess

class AuthInteractorImpl @Inject constructor(
    private val repo: AuthRepository,
    private val dataStoreHelper: DataStoreHelper
) : AuthInteractor {

    override suspend fun login(email: String, password: String): Result<Token> = withContext(Dispatchers.IO) {
        repo.login(UserLogin(email, password)).onSuccess { token ->
            syncSessionAfterLogin(
                token = token,
                fallbackLogin = email
            )
        }
    }

    override suspend fun getCurrentUser(): Result<UserProfile> =
        withContext(Dispatchers.IO) { repo.getCurrentUser() }

    override suspend fun register(user: UserRegisterParams): Result<UserRegister> =
        withContext(Dispatchers.IO) { repo.register(user) }

    override suspend fun registerAndLogin(user: UserRegisterParams): Result<Token> =
        withContext(Dispatchers.IO) {
            when (val result = repo.register(user)) {
                is Result.Success -> login(user.email, checkNotNull(user.password))
                is Result.Error -> Result.Error(result.code, result.message)
                is Result.Exception -> Result.Exception(result.error)
            }
        }

    private suspend fun syncSessionAfterLogin(
        token: Token,
        fallbackLogin: String
    ) {
        dataStoreHelper.saveSessionState(
            SessionState(
                token = token.accessToken,
                userLogin = fallbackLogin,
                userId = null
            )
        )

        val profile = (repo.getCurrentUser() as? Result.Success)?.data
        dataStoreHelper.saveSessionState(
            SessionState(
                token = token.accessToken,
                userLogin = profile?.email ?: fallbackLogin,
                userId = profile?.id
            )
        )
    }
}
