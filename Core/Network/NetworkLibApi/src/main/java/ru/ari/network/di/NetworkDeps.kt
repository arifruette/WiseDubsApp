package ru.ari.network.di

import retrofit2.Retrofit

interface NetworkDeps {

    @get:BaseRetrofit
    val baseRetrofitInstance: Retrofit

    @get:AuthRetrofit
    val authRetrofitInstance: Retrofit
}