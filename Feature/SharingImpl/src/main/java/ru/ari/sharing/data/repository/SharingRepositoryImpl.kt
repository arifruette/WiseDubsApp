package ru.ari.sharing.data.repository

import retrofit2.HttpException
import ru.ari.cache.domain.SharingPostDataSource
import ru.ari.network.domain.models.Result
import ru.ari.sharing.api.domain.models.Post
import ru.ari.sharing.api.domain.repository.SharingRepository
import ru.ari.sharing.data.api.PostsResponseApi
import ru.ari.sharing.data.mappers.toCacheModel
import ru.ari.sharing.data.mappers.toDomain
import javax.inject.Inject

class SharingRepositoryImpl @Inject constructor(
    private val postsResponseApi: PostsResponseApi,
    private val sharingPostDataSource: SharingPostDataSource
) : SharingRepository {

    override suspend fun getPosts(forceRefresh: Boolean): Result<List<Post>> {
        return try {
            val remotePosts = postsResponseApi.getPosts().map { it.toDomain() }
            sharingPostDataSource.savePosts(remotePosts.map { it.toCacheModel() })
            Result.Success(remotePosts)
        } catch (e: HttpException) {
            getCachedPostsOr { Result.Error(e.code(), e.message()) }
        } catch (e: Throwable) {
            getCachedPostsOr { Result.Exception(e) }
        }
    }

    override suspend fun getPostById(id: Long): Result<Post> {
        val cachedPost = sharingPostDataSource.getPostById(id)?.toDomain()
        if (cachedPost != null) {
            return Result.Success(cachedPost)
        }

        return when (val postsResult = getPosts(forceRefresh = true)) {
            is Result.Success -> {
                postsResult.data.firstOrNull { it.id == id }?.let { Result.Success(it) }
                    ?: Result.Error(404, "Post with id=$id not found")
            }

            is Result.Error -> postsResult
            is Result.Exception -> postsResult
        }
    }

    private suspend fun getCachedPostsOr(onEmptyCache: () -> Result<List<Post>>): Result<List<Post>> {
        val cachedPosts = sharingPostDataSource.getPosts().map { it.toDomain() }
        return if (cachedPosts.isNotEmpty()) {
            Result.Success(cachedPosts)
        } else {
            onEmptyCache()
        }
    }
}
