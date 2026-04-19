package ru.ari.posts.data.mappers

import ru.ari.cache.domain.models.CachedPickupLocation
import ru.ari.cache.domain.models.CachedPost
import ru.ari.cache.domain.models.CachedPostImage
import ru.ari.posts.api.domain.models.PickupLocation
import ru.ari.posts.api.domain.models.Post
import ru.ari.posts.api.domain.models.PostImage
import ru.ari.posts.data.models.ImageResponse
import ru.ari.posts.data.models.PickupLocationResponse
import ru.ari.posts.data.models.PostResponse

fun PostResponse.toDomain(baseUrl: String): Post = Post(
    id = id,
    title = title,
    description = description,
    exchange = exchange,
    pickupLocation = pickupLocation.toDomain(),
    messageId = messageId,
    isActive = isActive,
    isReserved = isReserved,
    createdAt = createdAt,
    reservedBy = reservedBy,
    authorEmail = authorEmail,
    images = images.map { it.toDomain(baseUrl) }
)

fun PickupLocationResponse.toDomain(): PickupLocation = PickupLocation(
    id = id,
    userId = userId,
    corpus = corpus,
    entrance = entrance,
    floor = floor,
    room = room,
    comment = comment,
    displayText = displayText,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun ImageResponse.toDomain(baseUrl: String): PostImage = PostImage(
    id = id,
    url = resolveImageUrl(url = url, baseUrl = baseUrl)
)

fun Post.toCacheModel(): CachedPost = CachedPost(
    id = id,
    title = title,
    description = description,
    pickupLocation = pickupLocation.toCacheModel(),
    isActive = isActive,
    isReserved = isReserved,
    exchange = exchange,
    reservedBy = reservedBy,
    authorEmail = authorEmail,
    messageId = messageId,
    createdAt = createdAt,
    images = images.map(PostImage::toCacheModel)
)

private fun PickupLocation.toCacheModel(): CachedPickupLocation = CachedPickupLocation(
    id = id,
    userId = userId,
    corpus = corpus,
    entrance = entrance,
    floor = floor,
    room = room,
    comment = comment,
    displayText = displayText,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun CachedPost.toDomain(): Post = Post(
    id = id,
    title = title,
    description = description,
    exchange = exchange,
    pickupLocation = pickupLocation.toDomain(),
    messageId = messageId,
    isActive = isActive,
    isReserved = isReserved,
    createdAt = createdAt,
    reservedBy = reservedBy,
    authorEmail = authorEmail,
    images = images.map(CachedPostImage::toDomain)
)

private fun CachedPickupLocation.toDomain(): PickupLocation = PickupLocation(
    id = id,
    userId = userId,
    corpus = corpus,
    entrance = entrance,
    floor = floor,
    room = room,
    comment = comment,
    displayText = displayText,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun PostImage.toCacheModel(): CachedPostImage = CachedPostImage(
    id = id,
    url = url
)

private fun CachedPostImage.toDomain(): PostImage = PostImage(
    id = id,
    url = url
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
