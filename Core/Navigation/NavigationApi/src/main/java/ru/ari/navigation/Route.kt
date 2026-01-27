package ru.ari.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
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
    }

}