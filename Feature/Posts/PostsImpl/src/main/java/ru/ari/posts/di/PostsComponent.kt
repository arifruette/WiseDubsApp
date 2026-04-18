package ru.ari.posts.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.ari.cache.di.CacheApi
import ru.ari.network.di.NetworkApi
import ru.ari.posts.api.di.PostsApi
import ru.ari.posts.di.modules.PostsBindsModule
import ru.ari.posts.di.modules.PostsDataModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [PostsBindsModule::class, PostsDataModule::class],
    dependencies = [NetworkApi::class, CacheApi::class]
)
interface PostsComponent : PostsApi {

    @Component.Factory
    interface Factory {
        fun create(
            networkApi: NetworkApi,
            cacheApi: CacheApi,
            @BindsInstance context: Context
        ): PostsComponent
    }
}
