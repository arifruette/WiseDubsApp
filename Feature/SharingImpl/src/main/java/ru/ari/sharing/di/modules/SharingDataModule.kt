package ru.ari.sharing.di.modules

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.create
import ru.ari.network.di.AuthRetrofit
import ru.ari.sharing.data.api.PostsResponseApi
import ru.ari.sharing.di.scope.SharingScreenScope

@Module
class SharingDataModule {

    @Provides
    @SharingScreenScope
    fun providePostsResponseApi(@AuthRetrofit retrofit: Retrofit): PostsResponseApi {
        return retrofit.create<PostsResponseApi>()
    }
}
