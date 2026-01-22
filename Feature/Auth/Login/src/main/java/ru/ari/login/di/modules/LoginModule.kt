package ru.ari.login.di.modules

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.ari.login.data.api.UserLoginApi
import ru.ari.login.di.scope.LoginScreenScope
import ru.ari.network.di.BaseRetrofit

@Module
class LoginModule {

    @LoginScreenScope
    @Provides
    fun provideUserLoginApi(
        @BaseRetrofit retrofit: Retrofit
    ): UserLoginApi = retrofit.create(UserLoginApi::class.java)

}