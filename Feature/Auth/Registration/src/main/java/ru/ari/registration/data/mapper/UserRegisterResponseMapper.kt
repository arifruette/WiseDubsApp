package ru.ari.registration.data.mapper

import ru.ari.registration.data.models.UserRegisterResponse
import ru.ari.registration.domain.models.UserRegister

fun UserRegisterResponse.toDomainUserRegisterModel(): UserRegister = UserRegister(
    email = this.email,
    telegramId = this.telegramId
)