package ru.ari.registration.domain.usecase

import ru.ari.network.domain.models.Result
import ru.ari.registration.domain.models.UserRegister
import ru.ari.registration.domain.repository.UserRegisterRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val usersRegisterRepository: UserRegisterRepository
) {
    suspend operator fun invoke(userRegister: UserRegister): Result<UserRegister> {
        return Result.Error(404, "Не удалось зарегестрировать пользователя")
    }
}