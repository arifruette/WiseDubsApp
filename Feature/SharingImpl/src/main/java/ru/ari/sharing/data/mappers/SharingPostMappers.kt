package ru.ari.sharing.data.mappers

import ru.ari.cache.domain.models.SharingPost
import ru.ari.cache.domain.models.SharingPostImage
import ru.ari.sharing.api.domain.models.Post
import ru.ari.sharing.api.domain.models.PostImage
import ru.ari.sharing.data.models.ImageResponse
import ru.ari.sharing.data.models.PostResponse

fun PostResponse.toDomain(): Post = Post(
    id = id,
    title = title,
    description = description,
    exchange = exchange,
    corpus = corpus,
    room = room,
    messageId = messageId,
    isActive = isActive,
    isReserved = isReserved,
    createdAt = createdAt,
    reservedBy = reservedBy,
    authorEmail = authorEmail,
    images = images.map(ImageResponse::toDomain)
)

private fun ImageResponse.toDomain(): PostImage = PostImage(
    id = id,
    url = url
)

fun Post.toCacheModel(): SharingPost = SharingPost(
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
    createdAt = createdAt,
    images = images.map(PostImage::toCacheModel)
)

fun SharingPost.toDomain(): Post = Post(
    id = id,
    title = title,
    description = description,
    exchange = exchange,
    corpus = corpus,
    room = room,
    messageId = messageId,
    isActive = isActive,
    isReserved = isReserved,
    createdAt = createdAt,
    reservedBy = reservedBy,
    authorEmail = authorEmail,
    images = images.map(SharingPostImage::toDomain)
)

private fun PostImage.toCacheModel(): SharingPostImage = SharingPostImage(
    id = id,
    url = url
)

private fun SharingPostImage.toDomain(): PostImage = PostImage(
    id = id,
    url = url
)
