package ru.ari.myposts.di.component

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.myposts.di.modules.MyPostsBindsModule
import ru.ari.myposts.di.scope.MyPostsScreenScope
import ru.ari.posts.api.di.PostsApi

@MyPostsScreenScope
@Component(
    modules = [MyPostsBindsModule::class],
    dependencies = [PostsApi::class]
)
interface MyPostsComponent {

    val myPostsViewModelFactory: ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(postsApi: PostsApi): MyPostsComponent
    }
}
