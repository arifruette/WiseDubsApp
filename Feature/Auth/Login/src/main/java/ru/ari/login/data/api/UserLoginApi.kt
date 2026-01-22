package ru.ari.login.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import ru.ari.login.data.models.TokenResponse
import ru.ari.login.data.models.UserLoginRequest

interface UserLoginApi {

    @POST("auth/login")
    suspend fun loginUser(
        @Body user: UserLoginRequest
    ): TokenResponse

}