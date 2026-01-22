package ru.ari.login.data.mapper

import ru.ari.login.data.models.UserLoginRequest
import ru.ari.login.domain.models.UserLogin

fun UserLogin.toRequestModel() = UserLoginRequest(
    email = this.email,
    password = this.password
)