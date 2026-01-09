package ru.ari.sharing.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.ari.navigation.Route
import ru.ari.sharing.presentation.ui.SharingScreen

fun EntryProviderScope<NavKey>.provideSharingScreen() =
    entry<Route.PostLogin.SharingScreenRoute> {
        SharingScreen()
    }