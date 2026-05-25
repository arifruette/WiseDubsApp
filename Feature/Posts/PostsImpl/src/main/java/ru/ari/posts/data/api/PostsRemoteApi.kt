package ru.ari.posts.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import kotlin.jvm.JvmSuppressWildcards
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.ari.posts.data.models.PostResponse

interface PostsRemoteApi {
    @GET("sharing/posts")
    suspend fun getAllPosts(): List<PostResponse>

    @GET("sharing/posts/feed")
    suspend fun getFeedPosts(): List<PostResponse>

    @GET("sharing/posts/me")
    suspend fun getMyPosts(): List<PostResponse>

    @GET("sharing/posts/reserved/me")
    suspend fun getReservedPosts(): List<PostResponse>

    @GET("sharing/posts/{id}")
    suspend fun getPostById(
        @Path("id") id: Long
    ): PostResponse

    @POST("sharing/posts/{id}/reserve")
    suspend fun reservePost(
        @Path("id") id: Long
    ): PostResponse

    @POST("sharing/posts/{id}/unreserve")
    suspend fun unreservePost(
        @Path("id") id: Long
    ): PostResponse

    @Multipart
    @POST("sharing/posts")
    suspend fun createPost(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("pickup_location_id") pickupLocationId: RequestBody,
        @Part("exchange") exchange: RequestBody?,
        @Part("message_id") messageId: RequestBody,
        @Part images: List<@JvmSuppressWildcards MultipartBody.Part>
    ): PostResponse

    @Multipart
    @PATCH("sharing/posts/{id}")
    suspend fun setPostActive(
        @Path("id") id: Long,
        @Part("is_active") isActive: RequestBody
    ): PostResponse

    @Multipart
    @PATCH("sharing/posts/{id}")
    suspend fun updatePost(
        @Path("id") id: Long,
        @Part("title") title: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("pickup_location_id") pickupLocationId: RequestBody?,
        @Part("exchange") exchange: RequestBody?,
        @Part("message_id") messageId: RequestBody?,
        @Part("retained_image_ids") retainedImageIds: List<@JvmSuppressWildcards RequestBody>?,
        @Part("clear_images") clearImages: RequestBody?,
        @Part images: List<@JvmSuppressWildcards MultipartBody.Part>
    ): PostResponse
}
