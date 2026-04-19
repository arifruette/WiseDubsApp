package ru.ari.posts.data.repository

import java.io.File
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import ru.ari.cache.domain.PostDataSource
import ru.ari.cache.domain.models.PostCacheScope
import ru.ari.network.di.AuthRetrofit
import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.models.CreatePostParams
import ru.ari.posts.api.domain.models.Post
import ru.ari.posts.api.domain.models.UpdatePostParams
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

        when (val myPostsResult = getMyPosts(forceRefresh = true)) {
            is Result.Success -> {
                myPostsResult.data.firstOrNull { it.id == id }?.let { return Result.Success(it) }
            }

            is Result.Error -> Unit
            is Result.Exception -> Unit
        }

        return when (val postsResult = getAllPosts(forceRefresh = true)) {
            is Result.Success -> {
                postsResult.data.firstOrNull { it.id == id }?.let { Result.Success(it) }
                    ?: Result.Error(404, "Пост с id=$id не найден")
            }

            is Result.Error -> postsResult
            is Result.Exception -> postsResult
        }
    }

    override suspend fun createPost(params: CreatePostParams): Result<Post> =
        try {
            val baseUrl = authRetrofit.baseUrl().toString()
            val createdPost = postsRemoteApi.createPost(
                title = params.title.toPart(),
                description = params.description.toNullablePart(),
                pickupLocationId = params.pickupLocationId.toString().toPart(),
                exchange = params.exchange.toNullablePart(),
                messageId = params.messageId.toPart(),
                reservedBy = params.reservedBy.toPart(),
                images = params.imageFiles.toImageParts()
            ).toDomain(baseUrl)

            clearPostsCache()
            Result.Success(createdPost)
        } catch (e: HttpException) {
            Result.Error(e.code(), e.message())
        } catch (e: Throwable) {
            Result.Exception(e)
        }

    override suspend fun updatePost(params: UpdatePostParams): Result<Post> =
        try {
            val baseUrl = authRetrofit.baseUrl().toString()
            val updatedPost = postsRemoteApi.updatePost(
                id = params.postId,
                title = params.title.toPart(),
                description = params.description.toNullablePart(),
                pickupLocationId = params.pickupLocationId?.toString()?.toPart(),
                exchange = params.exchange.toNullablePart(),
                messageId = params.messageId.toPart(),
                reservedBy = params.reservedBy.toPart(),
                images = params.imageFiles?.toImageParts().orEmpty()
            ).toDomain(baseUrl)

            clearPostsCache()
            Result.Success(updatedPost)
        } catch (e: HttpException) {
            Result.Error(e.code(), e.message())
        } catch (e: Throwable) {
            Result.Exception(e)
        }

    override suspend fun setPostActive(id: Long, isActive: Boolean): Result<Post> =
        try {
            val baseUrl = authRetrofit.baseUrl().toString()
            val updatedPost = postsRemoteApi.setPostActive(
                id = id,
                isActive = isActive.toString()
            ).toDomain(baseUrl)

            clearPostsCache()
            Result.Success(updatedPost)
        } catch (e: HttpException) {
            Result.Error(e.code(), e.message())
        } catch (e: Throwable) {
            Result.Exception(e)
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

    private suspend fun clearPostsCache() {
        postDataSource.clearPosts(PostCacheScope.MY)
        postDataSource.clearPosts(PostCacheScope.ALL)
    }

    private fun String.toPart(): RequestBody = RequestBody.create(MultipartBody.FORM, this)

    private fun String?.toNullablePart(): RequestBody? =
        this?.let { value -> RequestBody.create(MultipartBody.FORM, value) }

    private fun List<File>.toImageParts(): List<Part> = mapIndexed { index, file ->
        Part.createFormData(
            "images",
            file.name.ifBlank { "image_$index.jpg" },
            RequestBody.create(IMAGE_MEDIA_TYPE, file)
        )
    }

    private companion object {
        val IMAGE_MEDIA_TYPE: MediaType? = MediaType.parse("image/*")
    }
}
