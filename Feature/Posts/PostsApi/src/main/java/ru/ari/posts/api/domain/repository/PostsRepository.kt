package ru.ari.posts.api.domain.repository

import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.models.CreatePostParams
import ru.ari.posts.api.domain.models.Post
import ru.ari.posts.api.domain.models.UpdatePostParams

interface PostsRepository {
    suspend fun getAllPosts(forceRefresh: Boolean = false): Result<List<Post>>
    suspend fun getMyPosts(forceRefresh: Boolean = false): Result<List<Post>>
    suspend fun getPostById(id: Long): Result<Post>
    suspend fun createPost(params: CreatePostParams): Result<Post>
    suspend fun updatePost(params: UpdatePostParams): Result<Post>
    suspend fun setPostActive(id: Long, isActive: Boolean): Result<Post>
}
