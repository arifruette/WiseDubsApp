package ru.ari.cache.data.room

import ru.ari.cache.data.room.dao.SharingPostDao
import ru.ari.cache.data.room.entity.SharingPostEntity
import ru.ari.cache.data.room.entity.SharingPostImageEntity
import ru.ari.cache.data.room.models.SharingPostWithImages
import ru.ari.cache.domain.SharingPostDataSource
import ru.ari.cache.domain.models.SharingPost
import ru.ari.cache.domain.models.SharingPostImage
import javax.inject.Inject

class SharingPostLocalDataSource @Inject constructor(
    private val sharingPostDao: SharingPostDao
) : SharingPostDataSource {
    override suspend fun savePosts(posts: List<SharingPost>) {
        sharingPostDao.deleteAllPostImages()
        sharingPostDao.deleteAllPosts()
        sharingPostDao.insertPosts(posts.map { it.toEntity() })
        sharingPostDao.insertPostImages(posts.flatMap { it.toImageEntities() })
    }

    override suspend fun getPosts(): List<SharingPost> =
        sharingPostDao.getPosts().map { it.toDomain() }

    override suspend fun getPostById(id: Long): SharingPost? =
        sharingPostDao.getPostById(id)?.toDomain()

    override suspend fun deleteAllPosts() {
        sharingPostDao.deleteAllPostImages()
        sharingPostDao.deleteAllPosts()
    }
}

private fun SharingPost.toEntity(): SharingPostEntity = SharingPostEntity(
    id = id,
    title = title,
    description = description,
    corpus = corpus,
    room = room,
    isActive = isActive,
    isReserved = isReserved,
    exchange = exchange,
    reservedBy = reservedBy,
    authorEmail = authorEmail,
    messageId = messageId,
    createdAt = createdAt
)

private fun SharingPost.toImageEntities(): List<SharingPostImageEntity> = images.map { image ->
    SharingPostImageEntity(
        id = image.id,
        postId = id,
        url = image.url
    )
}

private fun SharingPostWithImages.toDomain(): SharingPost = SharingPost(
    id = post.id,
    title = post.title,
    description = post.description,
    corpus = post.corpus,
    room = post.room,
    isActive = post.isActive,
    isReserved = post.isReserved,
    exchange = post.exchange,
    reservedBy = post.reservedBy,
    authorEmail = post.authorEmail,
    messageId = post.messageId,
    createdAt = post.createdAt,
    images = images.map { image ->
        SharingPostImage(
            id = image.id,
            url = image.url
        )
    }
)
