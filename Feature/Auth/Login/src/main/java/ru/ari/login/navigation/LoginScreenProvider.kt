package ru.ari.login.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.ari.composelib.LocalRootNavigator
import ru.ari.login.presentation.ui.LoginScreen
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import javax.inject.Inject

class LoginScreenProvider @Inject constructor(): RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PreLogin.LoginScreenRoute> {
            val rootNavigator = LocalRootNavigator.current
            LoginScreen(
                navigateToPostLoginFlow = {
                    rootNavigator.navigate(Route.PostLogin)
                }
            )
        }
    }
}