package ru.ari.login.domain.usecase

import ru.ari.cache.domain.datastore.DataStoreHelper
import ru.ari.cache.domain.models.SessionState
import ru.ari.login.domain.models.Token
import ru.ari.login.domain.models.UserLogin
import ru.ari.login.domain.repository.UsersLoginRepository
import ru.ari.network.domain.models.Result
import ru.ari.network.domain.models.onSuccess
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val usersRepository: UsersLoginRepository,
    private val dataStoreHelper: DataStoreHelper,
) {
    suspend operator fun invoke(user: UserLogin): Result<Token> {
        return usersRepository.loginUser(user).onSuccess { token ->
            dataStoreHelper.saveSessionState(
                SessionState(token.accessToken, user.email)
            )
        }
    }
}