package ru.ari.composelib

import androidx.compose.runtime.staticCompositionLocalOf
import ru.ari.navigation.Navigator

val LocalRootNavigator = staticCompositionLocalOf<Navigator> {
    error("Root navigator wasn't provided")
}

val LocalPreLoginNavigator = staticCompositionLocalOf<Navigator> {
    error("PreLogin navigator wasn't provided")
}

val LocalPostLoginNavigator = staticCompositionLocalOf<Navigator> {
    error("PostLogin navigator wasn't provided")
}

fun interface AppMessageHost {
    suspend fun showMessage(message: String)
}

val LocalAppMessageHost = staticCompositionLocalOf<AppMessageHost> {
    error("App message host wasn't provided")
}
