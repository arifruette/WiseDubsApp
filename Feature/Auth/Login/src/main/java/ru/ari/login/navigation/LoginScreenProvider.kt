package ru.ari.login.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.ari.login.presentation.ui.LoginScreen
import ru.ari.navigation.Route

fun EntryProviderScope<NavKey>.provideLoginScreen() =
    entry<Route.PreLogin.LoginScreenRoute> {
        LoginScreen()
    }