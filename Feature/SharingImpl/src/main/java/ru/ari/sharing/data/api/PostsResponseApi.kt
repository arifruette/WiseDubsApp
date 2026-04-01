package ru.ari.sharing.data.api

import retrofit2.http.GET
import ru.ari.sharing.data.models.PostResponse

interface PostsResponseApi {
    @GET("sharing/posts")
    suspend fun getPosts(): List<PostResponse>
}
