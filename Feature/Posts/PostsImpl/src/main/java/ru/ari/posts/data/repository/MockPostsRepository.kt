package ru.ari.posts.data.repository

import javax.inject.Inject
import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.models.CreatePostParams
import ru.ari.posts.api.domain.models.Post
import ru.ari.posts.api.domain.models.UpdatePostParams
import ru.ari.posts.api.domain.repository.PostsRepository

class MockPostsRepository @Inject constructor(
    private val mockPostsDataSource: MockPostsDataSource
) : PostsRepository {

    override suspend fun getAllPosts(forceRefresh: Boolean): Result<List<Post>> =
        Result.Success(mockPostsDataSource.getAllPosts())

    override suspend fun getFeedPosts(forceRefresh: Boolean): Result<List<Post>> =
        Result.Success(mockPostsDataSource.getFeedPosts())

    override suspend fun getMyPosts(forceRefresh: Boolean): Result<List<Post>> =
        Result.Success(mockPostsDataSource.getMyPosts())

    override suspend fun getPostById(id: Long): Result<Post> =
        mockPostsDataSource.getPostById(id)
            ?.let { post -> Result.Success(post) }
            ?: Result.Error(404, "Post with id=$id was not found")

    override suspend fun reservePost(postId: Long): Result<Post> =
        mockPostsDataSource.reservePost(postId)
            ?.let { post -> Result.Success(post) }
            ?: Result.Error(404, "Post with id=$postId was not found")

    override suspend fun unreservePost(postId: Long): Result<Post> =
        mockPostsDataSource.unreservePost(postId)
            ?.let { post -> Result.Success(post) }
            ?: Result.Error(404, "Post with id=$postId was not found")

    override suspend fun createPost(params: CreatePostParams): Result<Post> =
        Result.Success(mockPostsDataSource.createPost(params))

    override suspend fun updatePost(params: UpdatePostParams): Result<Post> =
        mockPostsDataSource.updatePost(params)
            ?.let { post -> Result.Success(post) }
            ?: Result.Error(404, "Post with id=${params.postId} was not found")

    override suspend fun setPostActive(id: Long, isActive: Boolean): Result<Post> =
        mockPostsDataSource.setPostActive(id = id, isActive = isActive)
            ?.let { post -> Result.Success(post) }
            ?: Result.Error(404, "Post with id=$id was not found")
}
