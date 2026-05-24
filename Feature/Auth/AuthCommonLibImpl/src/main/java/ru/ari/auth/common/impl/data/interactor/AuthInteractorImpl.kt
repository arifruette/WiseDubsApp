package ru.ari.auth.common.impl.data.interactor

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.ari.auth.common.api.domain.interactor.AuthInteractor
import ru.ari.auth.common.api.domain.models.Token
import ru.ari.auth.common.api.domain.models.UserLogin
import ru.ari.auth.common.api.domain.models.UserProfile
import ru.ari.auth.common.api.domain.models.UserRegister
import ru.ari.auth.common.api.domain.models.params.UserRegisterParams
import ru.ari.auth.common.api.domain.repository.AuthRepository
import ru.ari.cache.domain.PostDataSource
import ru.ari.cache.domain.datastore.DataStoreHelper
import ru.ari.cache.domain.models.PostCacheScope
import ru.ari.cache.domain.models.SessionState
import ru.ari.network.domain.models.Result
import ru.ari.network.domain.models.onSuccess

class AuthInteractorImpl @Inject constructor(
    private val repo: AuthRepository,
    private val dataStoreHelper: DataStoreHelper,
    private val postDataSource: PostDataSource
) : AuthInteractor {

    override suspend fun login(email: String, password: String): Result<Token> =
        withContext(Dispatchers.IO) {
            when (val result = repo.login(UserLogin(email, password))) {
                is Result.Success -> syncSessionAfterLogin(
                    token = result.data,
                    fallbackLogin = email
                )
                is Result.Error -> Result.Error(result.code, result.message)
                is Result.Exception -> Result.Exception(result.error)
            }
        }

    override suspend fun getCurrentUser(): Result<UserProfile> =
        refreshCurrentUser()

    override fun observeCurrentUser(): Flow<UserProfile?> =
        dataStoreHelper.getSessionState().map { sessionState ->
            sessionState?.toUserProfile()
        }

    override suspend fun refreshCurrentUser(): Result<UserProfile> =
        withContext(Dispatchers.IO) {
            repo.getCurrentUser().onSuccess { profile ->
                syncSessionAfterProfileRefresh(profile)
            }
        }

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

    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            PostCacheScope.values().forEach { scope ->
                postDataSource.clearPosts(scope)
            }
            dataStoreHelper.eraseSessionState()
        }
    }

    private suspend fun syncSessionAfterLogin(
        token: Token,
        fallbackLogin: String
    ): Result<Token> {
        dataStoreHelper.saveSessionState(
            SessionState(
                token = token.accessToken,
                userLogin = fallbackLogin,
                userId = null
            )
        )

        return when (val profileResult = repo.getCurrentUser()) {
            is Result.Success -> {
                syncSessionAfterProfileRefresh(profileResult.data)
                Result.Success(token)
            }
            is Result.Error -> {
                dataStoreHelper.eraseSessionState()
                Result.Error(profileResult.code, profileResult.message)
            }
            is Result.Exception -> {
                dataStoreHelper.eraseSessionState()
                Result.Exception(profileResult.error)
            }
        }
    }

    private suspend fun syncSessionAfterProfileRefresh(profile: UserProfile) {
        val currentToken = dataStoreHelper.getSessionState()
            .firstOrNull()
            ?.token
            ?: return

        dataStoreHelper.saveSessionState(
            SessionState(
                token = currentToken,
                userLogin = profile.email,
                userId = profile.id,
                userTelegramId = profile.telegramId
            )
        )
    }

    private fun SessionState.toUserProfile(): UserProfile? {
        val id = userId ?: return null
        val telegramId = userTelegramId ?: return null

        return UserProfile(
            id = id,
            email = userLogin,
            telegramId = telegramId
        )
    }
}
