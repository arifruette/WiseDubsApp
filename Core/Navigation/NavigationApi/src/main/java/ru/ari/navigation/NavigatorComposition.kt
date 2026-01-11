package ru.ari.navigation

import androidx.compose.runtime.staticCompositionLocalOf

val LocalRootNavigator = staticCompositionLocalOf<Navigator> {
    error("Root navigator wasn't provided")
}

val LocalPreLoginNavigator = staticCompositionLocalOf<Navigator> {
    error("PreLogin navigator wasn't provided")
}

val LocalPostLoginNavigator = staticCompositionLocalOf<Navigator> {
    error("PostLogin navigator wasn't provided")
}
