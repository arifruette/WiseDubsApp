package ru.ari.posts.api.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.models.CreatePostParams
import ru.ari.posts.api.domain.models.Post
import ru.ari.posts.api.domain.models.UpdatePostParams

interface PostsInteractor {
    suspend fun getAllPosts(forceRefresh: Boolean = false): Result<List<Post>>
    fun observeAllPosts(): Flow<List<Post>>

    suspend fun getFeedPosts(forceRefresh: Boolean = false): Result<List<Post>>
    fun observeFeedPosts(): Flow<List<Post>>

    suspend fun getMyPosts(forceRefresh: Boolean = false): Result<List<Post>>
    fun observeMyPosts(): Flow<List<Post>>

    suspend fun getReservedPosts(forceRefresh: Boolean = false): Result<List<Post>>
    fun observeReservedPosts(): Flow<List<Post>>

    suspend fun getPostById(id: Long): Result<Post>

    suspend fun reservePost(postId: Long): Result<Post>
    suspend fun unreservePost(postId: Long): Result<Post>
    suspend fun createPost(params: CreatePostParams): Result<Post>
    suspend fun updatePost(params: UpdatePostParams): Result<Post>
    suspend fun setPostActive(id: Long, isActive: Boolean): Result<Post>
}
