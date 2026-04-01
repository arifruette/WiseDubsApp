package ru.ari.cache.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.ari.cache.data.room.entity.SharingPostEntity
import ru.ari.cache.data.room.entity.SharingPostImageEntity
import ru.ari.cache.data.room.models.SharingPostWithImages

@Dao
interface SharingPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<SharingPostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostImages(images: List<SharingPostImageEntity>)

    @Transaction
    @Query("SELECT * FROM user_posts")
    suspend fun getPosts(): List<SharingPostWithImages>

    @Transaction
    @Query("SELECT * FROM user_posts WHERE id = :id LIMIT 1")
    suspend fun getPostById(id: Long): SharingPostWithImages?

    @Query("DELETE FROM user_posts")
    suspend fun deleteAllPosts()

    @Query("DELETE FROM user_post_images")
    suspend fun deleteAllPostImages()
}
