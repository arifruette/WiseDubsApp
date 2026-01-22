package ru.ari.login.data.mapper

import ru.ari.login.data.models.TokenResponse
import ru.ari.login.domain.models.Token


fun TokenResponse.toDomainTokenModel(): Token = Token(
    accessToken = this.accessToken,
    tokenType = this.tokenType
)

