package ru.ari.posts.data.mappers

import ru.ari.cache.domain.models.CachedPost
import ru.ari.cache.domain.models.CachedPostImage
import ru.ari.posts.api.domain.models.GroupedRoom
import ru.ari.posts.api.domain.models.Post
import ru.ari.posts.api.domain.models.PostImage
import ru.ari.posts.api.domain.models.Room
import ru.ari.posts.data.models.GroupedRoomsResponse
import ru.ari.posts.data.models.ImageResponse
import ru.ari.posts.data.models.PostResponse
import ru.ari.posts.data.models.RoomResponse

fun PostResponse.toDomain(baseUrl: String): Post = Post(
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
    images = images.map { it.toDomain(baseUrl) }
)

private fun ImageResponse.toDomain(baseUrl: String): PostImage = PostImage(
    id = id,
    url = resolveImageUrl(url = url, baseUrl = baseUrl)
)

fun GroupedRoomsResponse.toDomain(): GroupedRoom = GroupedRoom(
    corpus = corpus,
    rooms = rooms.map(RoomResponse::toDomain)
)

fun Post.toCacheModel(): CachedPost = CachedPost(
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

fun CachedPost.toDomain(): Post = Post(
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
    images = images.map(CachedPostImage::toDomain)
)

private fun PostImage.toCacheModel(): CachedPostImage = CachedPostImage(
    id = id,
    url = url
)

private fun CachedPostImage.toDomain(): PostImage = PostImage(
    id = id,
    url = url
)

private fun RoomResponse.toDomain(): Room = Room(
    id = id,
    roomName = roomName,
    corpus = corpus
)

private fun resolveImageUrl(url: String, baseUrl: String): String {
    val trimmedUrl = url.trim()

    if (trimmedUrl.isEmpty()) {
        return trimmedUrl
    }

    val normalizedBaseUrl = baseUrl.trimEnd('/')
    val normalizedPath = if (trimmedUrl.startsWith("/")) trimmedUrl else "/$trimmedUrl"
    return normalizedBaseUrl + normalizedPath
}
