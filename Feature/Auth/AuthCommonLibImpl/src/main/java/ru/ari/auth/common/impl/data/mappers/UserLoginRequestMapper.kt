package ru.ari.auth.common.impl.data.mappers

import ru.ari.auth.common.impl.data.models.UserLoginBody
import ru.ari.auth.common.api.domain.models.UserLogin

fun UserLogin.toRequest() = UserLoginBody(
    email = this.email,
    password = this.password
)