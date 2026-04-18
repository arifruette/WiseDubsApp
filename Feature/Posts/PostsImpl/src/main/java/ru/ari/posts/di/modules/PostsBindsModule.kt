package ru.ari.posts.di.modules

import dagger.Binds
import dagger.Module
import ru.ari.posts.api.domain.interactor.PostsInteractor
import ru.ari.posts.api.domain.repository.PostsRepository
import ru.ari.posts.data.repository.MockPostsRepository
import ru.ari.posts.domain.interactor.PostsInteractorImpl

@Module
interface PostsBindsModule {

    @Binds
    fun bindPostsRepository(
        mockPostsRepository: MockPostsRepository
    ): PostsRepository

    @Binds
    fun bindPostsInteractor(
        postsInteractorImpl: PostsInteractorImpl
    ): PostsInteractor
}
