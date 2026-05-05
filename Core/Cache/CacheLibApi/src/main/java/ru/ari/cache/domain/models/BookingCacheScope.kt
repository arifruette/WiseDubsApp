package ru.ari.cache.domain.models

sealed interface BookingCacheScope {
    val key: String

    data class DayOverview(
        val roomId: Int,
        val date: String
    ) : BookingCacheScope {
        override val key: String = "DAY:$roomId:$date"
    }

    data object MyUpcoming : BookingCacheScope {
        override val key: String = "MY_UPCOMING"
    }

    data object MyPast : BookingCacheScope {
        override val key: String = "MY_PAST"
    }
}

