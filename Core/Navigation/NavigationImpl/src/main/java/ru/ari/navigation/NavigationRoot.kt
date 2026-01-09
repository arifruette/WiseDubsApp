package ru.ari.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.collections.immutable.persistentListOf
import ru.ari.navigation.common.Navigator
import ru.ari.navigation.common.rememberNavigationState
import ru.ari.navigation.common.toEntries
import ru.ari.navigation.postlogin.PostLoginNavigation
import ru.ari.navigation.prelogin.PreLoginNavigation

val ROOT_ROUTES = persistentListOf(
    Route.PreLogin,
    Route.PostLogin
)

@Composable
fun NavigationRoot(
    startRoute: Route,
    modifier: Modifier = Modifier
) {
    val rootNavigationState = rememberNavigationState(
        startRoute = startRoute,
        topLevelRoutes = ROOT_ROUTES
    )
    val rootNavigator = remember(rootNavigationState) {
        Navigator(rootNavigationState)
    }
    val rootEntryProvider = entryProvider<NavKey> {
        entry<Route.PreLogin> {
            PreLoginNavigation()
        }
        entry<Route.PostLogin> {
            PostLoginNavigation()
        }
    }
    NavDisplay(
        modifier = modifier,
        entries = rootNavigationState.toEntries(rootEntryProvider),
        onBack = rootNavigator::goBack
    )
}