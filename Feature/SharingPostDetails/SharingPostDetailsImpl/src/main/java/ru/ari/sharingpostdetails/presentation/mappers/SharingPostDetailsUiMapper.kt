package ru.ari.sharingpostdetails.presentation.mappers

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import ru.ari.posts.api.domain.models.PickupLocation
import ru.ari.posts.api.domain.models.Post
import ru.ari.sharingpostdetails.presentation.models.PostDetailsImageUiModel
import ru.ari.sharingpostdetails.presentation.models.PrimaryActionType
import ru.ari.sharingpostdetails.presentation.models.PrimaryActionUiModel
import ru.ari.sharingpostdetails.presentation.models.ReservationStatusUi
import ru.ari.sharingpostdetails.presentation.models.SharingPostDetailsUiModel

interface SharingPostDetailsUiMapper {
    fun map(
        post: Post,
        currentUserId: Long?,
        isPrimaryActionInProgress: Boolean
    ): SharingPostDetailsUiModel

    class Impl @Inject constructor() : SharingPostDetailsUiMapper {
        override fun map(
            post: Post,
            currentUserId: Long?,
            isPrimaryActionInProgress: Boolean
        ): SharingPostDetailsUiModel {
            val status = when {
                !post.isReserved -> ReservationStatusUi.AVAILABLE
                post.reservedById == currentUserId && currentUserId != null -> {
                    ReservationStatusUi.RESERVED_BY_ME
                }

                else -> ReservationStatusUi.RESERVED_BY_OTHER
            }

            val primaryAction = when (status) {
                ReservationStatusUi.AVAILABLE -> PrimaryActionUiModel(
                    type = PrimaryActionType.RESERVE,
                    label = "Забронировать",
                    isEnabled = true,
                    isLoading = isPrimaryActionInProgress
                )

                ReservationStatusUi.RESERVED_BY_ME -> PrimaryActionUiModel(
                    type = PrimaryActionType.UNRESERVE,
                    label = "Снять бронь",
                    isEnabled = true,
                    isLoading = isPrimaryActionInProgress
                )

                ReservationStatusUi.RESERVED_BY_OTHER -> PrimaryActionUiModel(
                    type = PrimaryActionType.NONE,
                    label = "Уже забронировано",
                    isEnabled = false,
                    isLoading = false
                )
            }

            return SharingPostDetailsUiModel(
                id = post.id,
                title = post.title,
                description = post.description,
                exchange = post.exchange.ifBlank { "Не указано" },
                authorEmail = post.authorEmail,
                authorTelegramId = post.authorTelegramId,
                createdAt = formatPublicationDate(post.createdAt),
                address = buildAddress(post.pickupLocation),
                addressComment = post.pickupLocation.comment?.trim()?.takeIf(String::isNotBlank),
                status = status,
                primaryAction = primaryAction,
                images = post.images.map { image ->
                    PostDetailsImageUiModel(
                        id = image.id,
                        url = image.url
                    )
                }.toImmutableList()
            )
        }

        private fun buildAddress(location: PickupLocation): String {
            val parts = listOfNotNull(
                location.corpus?.trim()?.takeIf(String::isNotBlank)?.let { "$it корпус" },
                location.entrance?.trim()?.takeIf(String::isNotBlank)?.let { "$it подъезд" },
                location.floor?.trim()?.takeIf(String::isNotBlank)?.let { "$it этаж" },
                location.room.trim().takeIf(String::isNotBlank)?.let { "$it комната" }
            )

            return parts.joinToString(separator = ", ").ifBlank { "Адрес не указан" }
        }

        private fun formatPublicationDate(rawDate: String): String {
            val trimmedDate = rawDate.trim()
            if (trimmedDate.isEmpty()) {
                return rawDate
            }

            val localDateTime = parseDateTime(trimmedDate) ?: return rawDate
            return localDateTime.format(PUBLICATION_DATE_FORMATTER)
        }

        private fun parseDateTime(rawDate: String): LocalDateTime? {
            val zoneId = ZoneId.systemDefault()

            return runCatching {
                OffsetDateTime.parse(rawDate)
                    .atZoneSameInstant(zoneId)
                    .toLocalDateTime()
            }.getOrNull()
                ?: runCatching {
                    Instant.parse(rawDate)
                        .atZone(zoneId)
                        .toLocalDateTime()
                }.getOrNull()
                ?: runCatching {
                    LocalDateTime.parse(rawDate)
                }.getOrNull()
        }

        private companion object {
            val PUBLICATION_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(
                "d MMMM yyyy, HH:mm",
                Locale.forLanguageTag("ru")
            )
        }
    }
}
