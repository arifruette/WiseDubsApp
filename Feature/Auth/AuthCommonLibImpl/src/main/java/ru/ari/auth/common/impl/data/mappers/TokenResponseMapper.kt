package ru.ari.auth.common.impl.data.mappers

import ru.ari.auth.common.impl.data.models.TokenResponse
import ru.ari.auth.common.api.domain.models.Token


fun TokenResponse.toDomainTokenModel(): Token = Token(
    accessToken = this.accessToken,
    tokenType = this.tokenType
)

