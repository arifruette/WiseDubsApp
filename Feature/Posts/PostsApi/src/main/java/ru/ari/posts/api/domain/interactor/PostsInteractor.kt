package ru.ari.posts.api.domain.interactor

import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.models.Post

interface PostsInteractor {
    suspend fun getAllPosts(forceRefresh: Boolean = false): Result<List<Post>>
    suspend fun getMyPosts(forceRefresh: Boolean = false): Result<List<Post>>
    suspend fun getPostById(id: Long): Result<Post>
}
