package ru.ari.sharingpostdetails.di.component

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.cache.di.CacheApi
import ru.ari.posts.api.di.PostsApi
import ru.ari.sharingpostdetails.di.modules.SharingPostDetailsBindsModule
import ru.ari.sharingpostdetails.di.scope.SharingPostDetailsScreenScope

@SharingPostDetailsScreenScope
@Component(
    modules = [SharingPostDetailsBindsModule::class],
    dependencies = [PostsApi::class, CacheApi::class]
)
interface SharingPostDetailsComponent {

    val sharingPostDetailsViewModelFactory: ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            postsApi: PostsApi,
            cacheApi: CacheApi
        ): SharingPostDetailsComponent
    }
}
