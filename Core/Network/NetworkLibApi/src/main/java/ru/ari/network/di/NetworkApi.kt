package ru.ari.network.di

import retrofit2.Retrofit

interface NetworkApi {

    @get:BaseRetrofit
    val baseRetrofitInstance: Retrofit

    @get:AuthRetrofit
    val authRetrofitInstance: Retrofit
}