package ru.ari.sharing.di.component

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.posts.api.di.PostsApi
import ru.ari.sharing.di.modules.SharingBindsModule
import ru.ari.sharing.di.scope.SharingScreenScope

@SharingScreenScope
@Component(
    modules = [SharingBindsModule::class],
    dependencies = [PostsApi::class]
)
interface SharingComponent {

    val sharingViewModelFactory: ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(postsApi: PostsApi): SharingComponent
    }
}
