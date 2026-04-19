package ru.ari.cache.domain

import ru.ari.cache.domain.models.CachedPost
import ru.ari.cache.domain.models.PostCacheScope

interface PostDataSource {
    suspend fun savePosts(scope: PostCacheScope, posts: List<CachedPost>)
    suspend fun getPosts(scope: PostCacheScope): List<CachedPost>
    suspend fun getPostById(id: Long): CachedPost?
    suspend fun clearPosts(scope: PostCacheScope)
}
