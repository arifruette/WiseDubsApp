package ru.ari.sharing.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.sharing.presentation.ui.SharingScreen
import javax.inject.Inject

class SharingScreenRouteProvider @Inject constructor(): RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PostLogin.SharingScreenRoute> {
            SharingScreen()
        }
    }
}
