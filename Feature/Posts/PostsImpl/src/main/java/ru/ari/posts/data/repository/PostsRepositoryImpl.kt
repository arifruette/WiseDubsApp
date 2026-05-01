package ru.ari.posts.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody
import org.json.JSONObject
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

class PostsRepositoryImpl @Inject constructor(
    private val postsRemoteApi: PostsRemoteApi,
    private val postDataSource: PostDataSource,
    @param:AuthRetrofit private val authRetrofit: Retrofit
) : PostsRepository {

    override suspend fun getAllPosts(forceRefresh: Boolean): Result<List<Post>> =
        updatePosts(
            scope = PostCacheScope.ALL,
            remoteCall = postsRemoteApi::getAllPosts
        )

    override fun observeAllPosts(): Flow<List<Post>> =
        postDataSource.observePosts(PostCacheScope.ALL).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getFeedPosts(forceRefresh: Boolean): Result<List<Post>> =
        updatePosts(
            scope = PostCacheScope.FEED,
            remoteCall = postsRemoteApi::getFeedPosts
        )

    override fun observeFeedPosts(): Flow<List<Post>> =
        postDataSource.observePosts(PostCacheScope.FEED).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getMyPosts(forceRefresh: Boolean): Result<List<Post>> =
        updatePosts(
            scope = PostCacheScope.MY,
            remoteCall = postsRemoteApi::getMyPosts
        )

    override fun observeMyPosts(): Flow<List<Post>> =
        postDataSource.observePosts(PostCacheScope.MY).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getPostById(id: Long): Result<Post> =
        loadSinglePost { postsRemoteApi.getPostById(id) }

    override suspend fun reservePost(postId: Long): Result<Post> =
        mutatePost { postsRemoteApi.reservePost(postId) }

    override suspend fun unreservePost(postId: Long): Result<Post> =
        mutatePost { postsRemoteApi.unreservePost(postId) }

    override suspend fun createPost(params: CreatePostParams): Result<Post> =
        try {
            val baseUrl = authRetrofit.baseUrl().toString()
            val createdPost = postsRemoteApi.createPost(
                title = params.title.toPart(),
                description = params.description.toNullablePart(),
                pickupLocationId = params.pickupLocationId.toString().toPart(),
                exchange = params.exchange.toNullablePart(),
                messageId = params.messageId.toPart(),
                images = params.imageFiles.toImageParts()
            ).toDomain(baseUrl)

            upsertPostInCache(scope = PostCacheScope.MY, post = createdPost)
            Result.Success(createdPost)
        } catch (e: HttpException) {
            e.toResultError()
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
                images = params.imageFiles?.toImageParts().orEmpty()
            ).toDomain(baseUrl)

            updatePostInExistingScopes(updatedPost)
            upsertPostInCache(scope = PostCacheScope.MY, post = updatedPost)
            Result.Success(updatedPost)
        } catch (e: HttpException) {
            e.toResultError()
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

            updatePostInExistingScopes(updatedPost)
            upsertPostInCache(scope = PostCacheScope.MY, post = updatedPost)
            Result.Success(updatedPost)
        } catch (e: HttpException) {
            e.toResultError()
        } catch (e: Throwable) {
            Result.Exception(e)
        }

    private suspend fun updatePosts(
        scope: PostCacheScope,
        remoteCall: suspend () -> List<PostResponse>
    ): Result<List<Post>> {
        return try {
            val baseUrl = authRetrofit.baseUrl().toString()
            val remotePosts = remoteCall().map { it.toDomain(baseUrl) }
            postDataSource.savePosts(scope, remotePosts.map { it.toCacheModel() })
            Result.Success(remotePosts)
        } catch (e: HttpException) {
            Result.Error(code = e.code(), message = e.toResultError().message)
        } catch (e: Throwable) {
            Result.Exception(e)
        }
    }

    private suspend fun loadSinglePost(remoteCall: suspend () -> PostResponse): Result<Post> =
        try {
            val baseUrl = authRetrofit.baseUrl().toString()
            Result.Success(remoteCall().toDomain(baseUrl))
        } catch (e: HttpException) {
            e.toResultError()
        } catch (e: Throwable) {
            Result.Exception(e)
        }

    private suspend fun mutatePost(remoteCall: suspend () -> PostResponse): Result<Post> =
        try {
            val baseUrl = authRetrofit.baseUrl().toString()
            val updatedPost = remoteCall().toDomain(baseUrl)
            updatePostInExistingScopes(updatedPost)
            Result.Success(updatedPost)
        } catch (e: HttpException) {
            e.toResultError()
        } catch (e: Throwable) {
            Result.Exception(e)
        }

    private suspend fun updatePostInExistingScopes(post: Post) {
        listOf(PostCacheScope.MY, PostCacheScope.ALL, PostCacheScope.FEED).forEach { scope ->
            val cachedPosts = postDataSource.getPosts(scope)
            if (cachedPosts.any { cachedPost -> cachedPost.id == post.id }) {
                val updatedPosts = cachedPosts
                    .map { cachedPost ->
                        if (cachedPost.id == post.id) {
                            post.toCacheModel()
                        } else {
                            cachedPost
                        }
                    }
                    .filterNot { cachedPost -> scope == PostCacheScope.FEED && !cachedPost.isActive }
                postDataSource.savePosts(scope = scope, posts = updatedPosts)
            }
        }
    }

    private suspend fun upsertPostInCache(scope: PostCacheScope, post: Post) {
        val cachedPosts = postDataSource.getPosts(scope)
        val postCacheModel = post.toCacheModel()
        val updatedPosts = if (cachedPosts.any { cachedPost -> cachedPost.id == post.id }) {
            cachedPosts.map { cachedPost ->
                if (cachedPost.id == post.id) {
                    postCacheModel
                } else {
                    cachedPost
                }
            }
        } else {
            listOf(postCacheModel) + cachedPosts
        }
        postDataSource.savePosts(scope = scope, posts = updatedPosts)
    }

    private fun HttpException.toResultError(): Result.Error {
        val detail = response()
            ?.errorBody()
            ?.string()
            ?.let { body -> parseErrorDetail(body) }
            ?.takeIf(String::isNotBlank)

        return Result.Error(
            code = code(),
            message = detail ?: "Неизвестная ошибка"
        )
    }

    private fun parseErrorDetail(body: String): String? =
        runCatching { JSONObject(body).optString("detail") }.getOrNull()

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
