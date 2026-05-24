package ru.ari.auth.common.impl.di.module

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.create
import ru.ari.auth.common.impl.data.api.AuthRetrofitApi
import ru.ari.auth.common.impl.di.scope.AuthScope
import ru.ari.network.di.AuthRetrofit

@Module
class AuthCommonModule {

    @Provides
    @AuthScope
    fun provideAuthRetrofitApi(@AuthRetrofit retrofit: Retrofit): AuthRetrofitApi {
        return retrofit.create<AuthRetrofitApi>()
    }

}
