package ru.ari.auth.common.api.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.ari.auth.common.api.domain.models.Token
import ru.ari.auth.common.api.domain.models.UserProfile
import ru.ari.auth.common.api.domain.models.UserRegister
import ru.ari.auth.common.api.domain.models.params.UserRegisterParams
import ru.ari.network.domain.models.Result

interface AuthInteractor {
  suspend fun login(email: String, password: String): Result<Token>
  fun observeCurrentUser(): Flow<UserProfile?>
  suspend fun getCurrentUser(): Result<UserProfile>
  suspend fun refreshCurrentUser(): Result<UserProfile>
  suspend fun register(user: UserRegisterParams): Result<UserRegister>
  suspend fun registerAndLogin(user: UserRegisterParams): Result<Token>
  suspend fun logout()
}
