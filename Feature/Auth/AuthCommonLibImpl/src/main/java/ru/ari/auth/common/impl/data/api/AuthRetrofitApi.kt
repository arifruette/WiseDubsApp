package ru.ari.auth.common.impl.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import ru.ari.auth.common.impl.data.models.TokenResponse
import ru.ari.auth.common.impl.data.models.UserLoginBody
import ru.ari.auth.common.impl.data.models.UserRegisterBody
import ru.ari.auth.common.impl.data.models.UserRegisterResponse

interface AuthRetrofitApi {

    @POST("auth/login")
    suspend fun loginUser(
        @Body user: UserLoginBody
    ): TokenResponse

    @POST("auth/register")
    suspend fun registerUser(
        @Body user: UserRegisterBody
    ): UserRegisterResponse

}