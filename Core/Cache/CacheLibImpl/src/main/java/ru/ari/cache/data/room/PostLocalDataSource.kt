package ru.ari.cache.data.room

import ru.ari.cache.data.room.dao.PostDao
import ru.ari.cache.data.room.entity.CachedPostEntity
import ru.ari.cache.data.room.entity.CachedPostImageEntity
import ru.ari.cache.data.room.models.CachedPostWithImages
import ru.ari.cache.domain.PostDataSource
import ru.ari.cache.domain.models.CachedPickupLocation
import ru.ari.cache.domain.models.CachedPost
import ru.ari.cache.domain.models.CachedPostImage
import ru.ari.cache.domain.models.PostCacheScope
import javax.inject.Inject

class PostLocalDataSource @Inject constructor(
    private val postDao: PostDao
) : PostDataSource {

    override suspend fun savePosts(scope: PostCacheScope, posts: List<CachedPost>) {
        postDao.deletePostImagesByScope(scope.name)
        postDao.deletePostsByScope(scope.name)
        postDao.insertPosts(posts.map { it.toEntity(scope) })
        postDao.insertPostImages(posts.flatMap { it.toImageEntities(scope) })
    }

    override suspend fun getPosts(scope: PostCacheScope): List<CachedPost> =
        postDao.getPosts(scope.name).map { it.toDomain() }

    override suspend fun getPostById(id: Long): CachedPost? =
        postDao.getPostById(id)?.toDomain()

    override suspend fun clearPosts(scope: PostCacheScope) {
        postDao.deletePostImagesByScope(scope.name)
        postDao.deletePostsByScope(scope.name)
    }
}

private fun CachedPost.toEntity(scope: PostCacheScope): CachedPostEntity = CachedPostEntity(
    cacheKey = cacheKey(scope, id),
    postId = id,
    scope = scope.name,
    title = title,
    description = description,
    locationId = pickupLocation.id,
    locationUserId = pickupLocation.userId,
    locationCorpus = pickupLocation.corpus,
    locationEntrance = pickupLocation.entrance,
    locationFloor = pickupLocation.floor,
    locationRoom = pickupLocation.room,
    locationComment = pickupLocation.comment,
    locationDisplayText = pickupLocation.label,
    locationCreatedAt = pickupLocation.createdAt,
    locationUpdatedAt = pickupLocation.updatedAt,
    isActive = isActive,
    isReserved = isReserved,
    exchange = exchange,
    reservedBy = reservedBy,
    reservedById = reservedById,
    authorEmail = authorEmail,
    authorTelegramId = authorTelegramId,
    messageId = messageId,
    createdAt = createdAt
)

private fun CachedPost.toImageEntities(scope: PostCacheScope): List<CachedPostImageEntity> = images.map { image ->
    CachedPostImageEntity(
        postCacheKey = cacheKey(scope, id),
        imageId = image.id,
        url = image.url
    )
}

private fun CachedPostWithImages.toDomain(): CachedPost = CachedPost(
    id = post.postId,
    title = post.title,
    description = post.description,
    pickupLocation = CachedPickupLocation(
        id = post.locationId,
        userId = post.locationUserId,
        corpus = post.locationCorpus,
        entrance = post.locationEntrance,
        floor = post.locationFloor,
        room = post.locationRoom,
        comment = post.locationComment,
        label = post.locationDisplayText,
        createdAt = post.locationCreatedAt,
        updatedAt = post.locationUpdatedAt
    ),
    isActive = post.isActive,
    isReserved = post.isReserved,
    exchange = post.exchange,
    reservedBy = post.reservedBy,
    reservedById = post.reservedById,
    authorEmail = post.authorEmail,
    authorTelegramId = post.authorTelegramId,
    messageId = post.messageId,
    createdAt = post.createdAt,
    images = images.map { image ->
        CachedPostImage(
            id = image.imageId,
            url = image.url
        )
    }
)

private fun cacheKey(scope: PostCacheScope, postId: Long): String = "${scope.name}:$postId"
