package ru.ari.sharing.api.domain.repository

import ru.ari.network.domain.models.Result
import ru.ari.sharing.api.domain.models.Post

interface SharingRepository {
    suspend fun getPosts(forceRefresh: Boolean = false): Result<List<Post>>
    suspend fun getPostById(id: Long): Result<Post>
}
