package ru.ari.posts.api.di

import ru.ari.posts.api.domain.interactor.PostsInteractor

interface PostsApi {
    val postsInteractor: PostsInteractor
}
