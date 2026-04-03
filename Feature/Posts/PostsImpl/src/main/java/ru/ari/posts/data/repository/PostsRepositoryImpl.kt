package ru.ari.posts.data.repository

import retrofit2.HttpException
import retrofit2.Retrofit
import ru.ari.cache.domain.PostDataSource
import ru.ari.cache.domain.models.PostCacheScope
import ru.ari.network.di.AuthRetrofit
import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.models.Post
import ru.ari.posts.api.domain.repository.PostsRepository
import ru.ari.posts.data.api.PostsRemoteApi
import ru.ari.posts.data.mappers.toCacheModel
import ru.ari.posts.data.mappers.toDomain
import ru.ari.posts.data.models.PostResponse
import javax.inject.Inject

class PostsRepositoryImpl @Inject constructor(
    private val postsRemoteApi: PostsRemoteApi,
    private val postDataSource: PostDataSource,
    @param:AuthRetrofit private val authRetrofit: Retrofit
) : PostsRepository {

    override suspend fun getAllPosts(forceRefresh: Boolean): Result<List<Post>> =
        getPosts(
            scope = PostCacheScope.ALL,
            forceRefresh = forceRefresh,
            remoteCall = postsRemoteApi::getAllPosts
        )

    override suspend fun getMyPosts(forceRefresh: Boolean): Result<List<Post>> =
        getPosts(
            scope = PostCacheScope.MY,
            forceRefresh = forceRefresh,
            remoteCall = postsRemoteApi::getMyPosts
        )

    override suspend fun getPostById(id: Long): Result<Post> {
        val cachedPost = postDataSource.getPostById(id)?.toDomain()
        if (cachedPost != null) {
            return Result.Success(cachedPost)
        }

        return when (val postsResult = getAllPosts(forceRefresh = true)) {
            is Result.Success -> {
                postsResult.data.firstOrNull { it.id == id }?.let { Result.Success(it) }
                    ?: Result.Error(404, "Post with id=$id not found")
            }

            is Result.Error -> postsResult
            is Result.Exception -> postsResult
        }
    }

    private suspend fun getPosts(
        scope: PostCacheScope,
        forceRefresh: Boolean,
        remoteCall: suspend () -> List<PostResponse>
    ): Result<List<Post>> {
        if (!forceRefresh) {
            val cachedPosts = postDataSource.getPosts(scope).map { it.toDomain() }
            if (cachedPosts.isNotEmpty()) {
                return Result.Success(cachedPosts)
            }
        }

        return try {
            val baseUrl = authRetrofit.baseUrl().toString()
            val remotePosts = remoteCall().map { it.toDomain(baseUrl) }
            postDataSource.savePosts(scope, remotePosts.map { it.toCacheModel() })
            Result.Success(remotePosts)
        } catch (e: HttpException) {
            getCachedPostsOr(scope) { Result.Error(e.code(), e.message()) }
        } catch (e: Throwable) {
            getCachedPostsOr(scope) { Result.Exception(e) }
        }
    }

    private suspend fun getCachedPostsOr(
        scope: PostCacheScope,
        onEmptyCache: () -> Result<List<Post>>
    ): Result<List<Post>> {
        val cachedPosts = postDataSource.getPosts(scope).map { it.toDomain() }
        return if (cachedPosts.isNotEmpty()) {
            Result.Success(cachedPosts)
        } else {
            onEmptyCache()
        }
    }
}
