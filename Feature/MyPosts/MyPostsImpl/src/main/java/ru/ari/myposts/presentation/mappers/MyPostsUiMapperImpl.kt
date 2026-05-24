package ru.ari.myposts.presentation.mappers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Unarchive
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import ru.ari.myposts.domain.mapper.MyPostsUiMapper
import ru.ari.myposts.presentation.models.MyPostUiModel
import ru.ari.posts.api.domain.models.PickupLocation
import ru.ari.posts.api.domain.models.Post

class MyPostsUiMapperImpl @Inject constructor() : MyPostsUiMapper {

    override fun map(posts: List<Post>) = posts.map(::map).toImmutableList()

    override fun map(post: Post): MyPostUiModel = MyPostUiModel(
        id = post.id,
        title = post.title,
        exchangeText = "Обмен: ${post.exchange}",
        locationText = post.pickupLocation.displayText(),
        createdAgoText = post.createdAt.toRelativeTimeText(),
        isActive = post.isActive,
        previewImageUrl = post.images.firstOrNull()?.url,
        archiveIcon = if (post.isActive) {
            Icons.Default.Archive
        } else {
            Icons.Default.Unarchive
        },
        archiveActionTargetActive = !post.isActive
    )

    private fun PickupLocation.displayText(): String {
        val customLabel = label?.trim().orEmpty()
        if (customLabel.isNotBlank()) {
            return customLabel
        }

        val parts = listOfNotNull(
            corpus?.trim()?.takeIf(String::isNotBlank)?.let { "$it корпус" },
            entrance?.trim()?.takeIf(String::isNotBlank)?.let { "$it подъезд" },
            floor?.trim()?.takeIf(String::isNotBlank)?.let { "$it этаж" },
            room.trim().takeIf(String::isNotBlank)?.let { "$it комната" }
        )

        return parts.joinToString(separator = ", ").ifBlank { "Адрес не указан" }
    }

    private fun String.toRelativeTimeText(): String {
        val createdAt = parseCreatedAt(trim()) ?: return this
        val duration = Duration.between(createdAt, ZonedDateTime.now(createdAt.zone))

        return when {
            duration.isNegative || duration.toMinutes() < 1 -> "только что"
            duration.toHours() < 1 -> "${duration.toMinutes()} мин. назад"
            duration.toDays() < 1 -> "${duration.toHours()} ч. назад"
            duration.toDays() < 7 -> "${duration.toDays()} д. назад"
            else -> createdAt.format(PUBLICATION_DATE_FORMATTER)
        }
    }

    private fun parseCreatedAt(rawDate: String): ZonedDateTime? {
        if (rawDate.isBlank()) {
            return null
        }

        val zoneId = ZoneId.systemDefault()

        return runCatching { OffsetDateTime.parse(rawDate).atZoneSameInstant(zoneId) }.getOrNull()
            ?: runCatching { Instant.parse(rawDate).atZone(zoneId) }.getOrNull()
            ?: runCatching { LocalDateTime.parse(rawDate).atZone(zoneId) }.getOrNull()
    }

    private companion object {
        val PUBLICATION_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(
            "d MMMM yyyy",
            Locale.forLanguageTag("ru")
        )
    }
}
