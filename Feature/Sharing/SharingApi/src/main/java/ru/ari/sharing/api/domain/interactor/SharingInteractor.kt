package ru.ari.sharing.api.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.models.Post

interface SharingInteractor {
    suspend fun getPosts(forceRefresh: Boolean = false): Result<List<Post>>
    fun observePosts(): Flow<List<Post>>

    suspend fun getPostById(id: Long): Result<Post>
}
