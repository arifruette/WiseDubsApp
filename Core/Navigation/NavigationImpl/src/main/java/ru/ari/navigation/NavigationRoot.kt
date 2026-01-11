package ru.ari.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.navigation.postlogin.PostLoginNavigation
import ru.ari.navigation.prelogin.PreLoginNavigation

val ROOT_ROUTES = persistentListOf(
    Route.PreLogin,
    Route.PostLogin
)

@Composable
fun NavigationRoot(
    startRoute: Route,
    preLoginRoutes: ImmutableList<RouteEntryProvider>,
    postLoginRoutes: ImmutableList<RouteEntryProvider>,
    modifier: Modifier = Modifier
) {
    val rootNavigationState = rememberNavigationState(
        startRoute = startRoute,
        topLevelRoutes = ROOT_ROUTES
    )
    val rootNavigator = remember(rootNavigationState) {
        RootNavigatorImpl(rootNavigationState)
    }
    CompositionLocalProvider(
        LocalRootNavigator provides rootNavigator
    ) {
        val rootEntryProvider = entryProvider<NavKey> {
            entry<Route.PreLogin> {
                PreLoginNavigation(preLoginRoutes)
            }
            entry<Route.PostLogin> {
                PostLoginNavigation(postLoginRoutes)
            }
        }
        NavDisplay(
            modifier = modifier,
            entries = rootNavigationState.toEntries(rootEntryProvider),
            onBack = rootNavigator::goBack
        )
    }
}