package ru.ari.login.data.models

import com.google.gson.annotations.SerializedName

data class TokenResponse (
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String
)