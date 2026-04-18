package ru.ari.posts.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.ari.posts.data.models.GroupedRoomsResponse
import ru.ari.posts.data.models.PostResponse

interface PostsRemoteApi {
    @GET("sharing/posts")
    suspend fun getAllPosts(): List<PostResponse>

    @GET("sharing/posts/me")
    suspend fun getMyPosts(): List<PostResponse>

    @GET("rooms/grouped")
    suspend fun getGroupedRooms(): List<GroupedRoomsResponse>

    @Multipart
    @POST("sharing/posts")
    suspend fun createPost(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("corpus") corpus: RequestBody,
        @Part("room") room: RequestBody,
        @Part("exchange") exchange: RequestBody?,
        @Part("message_id") messageId: RequestBody,
        @Part("reserved_by") reservedBy: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): PostResponse

    @Multipart
    @PATCH("sharing/posts/{id}")
    suspend fun setPostActive(
        @Path("id") id: Long,
        @Part("is_active") isActive: String
    ): PostResponse

    @Multipart
    @PATCH("sharing/posts/{id}")
    suspend fun updatePost(
        @Path("id") id: Long,
        @Part("title") title: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("corpus") corpus: RequestBody?,
        @Part("room") room: RequestBody?,
        @Part("exchange") exchange: RequestBody?,
        @Part("message_id") messageId: RequestBody?,
        @Part("reserved_by") reservedBy: RequestBody?,
        @Part images: List<MultipartBody.Part>
    ): PostResponse
}
