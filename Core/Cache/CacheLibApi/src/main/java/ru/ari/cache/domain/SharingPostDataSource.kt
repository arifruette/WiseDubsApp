package ru.ari.cache.domain

import ru.ari.cache.domain.models.SharingPost

interface SharingPostDataSource {
    suspend fun savePosts(posts: List<SharingPost>)
    suspend fun getPosts(): List<SharingPost>
    suspend fun getPostById(id: Long): SharingPost?
    suspend fun deleteAllPosts()
}
