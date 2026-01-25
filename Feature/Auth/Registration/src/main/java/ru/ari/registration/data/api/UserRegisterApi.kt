package ru.ari.registration.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import ru.ari.registration.data.models.UserRegisterResponse
import ru.ari.registration.domain.models.UserRegister

interface UserRegisterApi {

    @POST("auth/register")
    suspend fun registerUser(
        @Body user: UserRegister
    ): UserRegisterResponse

}