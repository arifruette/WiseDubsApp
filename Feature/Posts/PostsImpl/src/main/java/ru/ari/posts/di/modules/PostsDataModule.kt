package ru.ari.posts.di.modules

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.create
import ru.ari.network.di.AuthRetrofit
import ru.ari.posts.data.api.PostsRemoteApi
import javax.inject.Singleton

@Module
class PostsDataModule {

    @Provides
    @Singleton
    fun providePostsRemoteApi(@AuthRetrofit retrofit: Retrofit): PostsRemoteApi {
        return retrofit.create<PostsRemoteApi>()
    }
}
