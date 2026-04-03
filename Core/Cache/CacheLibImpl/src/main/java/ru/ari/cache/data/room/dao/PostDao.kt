package ru.ari.cache.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.ari.cache.data.room.entity.CachedPostEntity
import ru.ari.cache.data.room.entity.CachedPostImageEntity
import ru.ari.cache.data.room.models.CachedPostWithImages

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<CachedPostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostImages(images: List<CachedPostImageEntity>)

    @Transaction
    @Query("SELECT * FROM cached_posts WHERE scope = :scope")
    suspend fun getPosts(scope: String): List<CachedPostWithImages>

    @Transaction
    @Query("SELECT * FROM cached_posts WHERE postId = :id LIMIT 1")
    suspend fun getPostById(id: Long): CachedPostWithImages?

    @Query("DELETE FROM cached_post_images WHERE postCacheKey IN (SELECT cacheKey FROM cached_posts WHERE scope = :scope)")
    suspend fun deletePostImagesByScope(scope: String)

    @Query("DELETE FROM cached_posts WHERE scope = :scope")
    suspend fun deletePostsByScope(scope: String)
}
