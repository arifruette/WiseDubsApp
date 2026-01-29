package ru.ari.auth.common.impl.data.mappers

import ru.ari.auth.common.impl.data.models.UserRegisterBody
import ru.ari.auth.common.impl.data.models.UserRegisterResponse
import ru.ari.auth.common.api.domain.models.UserRegister
import ru.ari.auth.common.api.domain.models.params.UserRegisterParams

fun UserRegisterResponse.toDomainUserRegisterModel(): UserRegister = UserRegister(
    email = this.email,
    telegramId = this.telegramId,
    id = this.id
)

fun UserRegisterParams.toRequest() = UserRegisterBody(
    email = this.email,
    telegramId = this.telegramId,
    password = this.password
)