package ru.ari.managepost.presentation.ui

import ru.ari.posts.api.domain.models.PickupLocation

internal fun PickupLocation.displayTitle(): String {
    val customTitle = label?.trim().orEmpty()
    return customTitle.ifBlank { buildSummary() }
}

internal fun PickupLocation.displaySecondaryText(): String {
    val summary = buildSummary()
    val commentText = comment?.trim().orEmpty()
    val hasCustomTitle = !label?.trim().isNullOrEmpty()

    return when {
        hasCustomTitle -> listOf(summary, commentText)
            .filter(String::isNotBlank)
            .joinToString(separator = " • ")
        else -> commentText
    }
}
