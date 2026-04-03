package ru.ari.posts.data.api

import retrofit2.http.GET
import ru.ari.posts.data.models.PostResponse

interface PostsRemoteApi {
    @GET("sharing/posts")
    suspend fun getAllPosts(): List<PostResponse>

    @GET("sharing/posts/me")
    suspend fun getMyPosts(): List<PostResponse>
}
