package ru.ari.network.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ari.network.AuthInterceptor
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    @BaseRetrofit
    fun provideBaseRetrofit(
        @BaseUrl url: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setFieldNamingPolicy(
                    FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
                ).create()
            )
        ).build()

    @Provides
    fun provideAuthOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        @BaseUrl url: String,
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setFieldNamingPolicy(
                        FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
                    ).create()
                )
            ).build()
}