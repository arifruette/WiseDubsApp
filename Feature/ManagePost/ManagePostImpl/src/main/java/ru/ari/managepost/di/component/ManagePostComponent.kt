package ru.ari.managepost.di.component

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.managepost.di.modules.ManagePostBindsModule
import ru.ari.managepost.di.modules.ManagePostDataModule
import ru.ari.managepost.di.scope.ManagePostScreenScope
import ru.ari.network.di.NetworkApi
import ru.ari.posts.api.di.PostsApi

@ManagePostScreenScope
@Component(
    modules = [
        ManagePostBindsModule::class,
        ManagePostDataModule::class
    ],
    dependencies = [
        PostsApi::class,
        NetworkApi::class
    ]
)
interface ManagePostComponent {

    val managePostViewModelFactory: ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            postsApi: PostsApi,
            networkApi: NetworkApi
        ): ManagePostComponent
    }
}
