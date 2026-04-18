package ru.ari.managepost.di.component

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.managepost.di.modules.ManagePostBindsModule
import ru.ari.managepost.di.scope.ManagePostScreenScope
import ru.ari.posts.api.di.PostsApi

@ManagePostScreenScope
@Component(
    modules = [ManagePostBindsModule::class],
    dependencies = [PostsApi::class]
)
interface ManagePostComponent {

    val managePostViewModelFactory: ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(postsApi: PostsApi): ManagePostComponent
    }
}
