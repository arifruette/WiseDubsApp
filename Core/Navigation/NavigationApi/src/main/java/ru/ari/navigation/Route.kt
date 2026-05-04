package ru.ari.navigation

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
@Stable
sealed interface Route : NavKey {

    @Serializable
    data object PreLogin : Route {

        @Serializable
        data object LoginScreenRoute : Route

        @Serializable
        data object RegistrationScreenRoute : Route
    }

    @Serializable
    data object PostLogin : Route {

        @Serializable
        data object SharingScreenRoute : Route

        @Serializable
        data object BookingScreenRoute : Route

        @Serializable
        data class SharingPostDetailsRoute(
            val postId: Long,
            val autoReserve: Boolean = false
        ) : Route

        @Serializable
        data object PostsScreenRoute : Route

        @Serializable
        data object CreatePostScreenRoute : Route

        @Serializable
        data class EditPostScreenRoute(val postId: Long) : Route

        @Serializable
        data class AddressManageScreenRoute(val locationId: Int? = null) : Route

        @Serializable
        data object ProfileScreenRoute : Route

        @Serializable
        data object ReservedPostsScreenRoute : Route
    }

}
