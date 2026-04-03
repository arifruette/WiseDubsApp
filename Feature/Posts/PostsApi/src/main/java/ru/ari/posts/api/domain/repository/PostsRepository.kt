package ru.ari.posts.api.domain.repository

import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.models.Post

interface PostsRepository {
    suspend fun getAllPosts(forceRefresh: Boolean = false): Result<List<Post>>
    suspend fun getMyPosts(forceRefresh: Boolean = false): Result<List<Post>>
    suspend fun getPostById(id: Long): Result<Post>
}
