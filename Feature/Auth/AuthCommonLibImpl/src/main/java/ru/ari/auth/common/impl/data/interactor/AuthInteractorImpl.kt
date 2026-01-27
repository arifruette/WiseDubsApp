package ru.ari.auth.common.impl.data.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ari.auth.common.api.domain.interactor.AuthInteractor
import ru.ari.auth.common.api.domain.models.Token
import ru.ari.auth.common.api.domain.models.UserLogin
import ru.ari.auth.common.api.domain.models.UserRegister
import ru.ari.auth.common.api.domain.models.params.UserRegisterParams
import ru.ari.auth.common.api.domain.repository.AuthRepository
import ru.ari.cache.domain.datastore.DataStoreHelper
import ru.ari.cache.domain.models.SessionState
import ru.ari.network.domain.models.onSuccess
import javax.inject.Inject
import ru.ari.network.domain.models.Result

class AuthInteractorImpl @Inject constructor(
    private val repo: AuthRepository,
    private val dataStoreHelper: DataStoreHelper
) : AuthInteractor {

    override suspend fun login(email: String, password: String): Result<Token> =
        withContext(Dispatchers.IO) {
            repo.login(UserLogin(email, password)).onSuccess { token ->
                dataStoreHelper.saveSessionState(
                    SessionState(
                        token = token.accessToken,
                        userLogin = email
                    )
                )
            }
        }

    override suspend fun register(user: UserRegisterParams): Result<UserRegister> =
        withContext(Dispatchers.IO) { repo.register(user) }

    override suspend fun registerAndLogin(user: UserRegisterParams): Result<Token> =
        withContext(Dispatchers.IO) {
            repo.register(user).onSuccess {
                return@withContext login(user.email, user.password)
            }
            when (val r = repo.register(user)) {
                is Result.Success -> login(user.email, checkNotNull(user.password))
                is Result.Error -> Result.Error(r.code, r.message)
                is Result.Exception -> Result.Exception(r.error)
            }
        }
}