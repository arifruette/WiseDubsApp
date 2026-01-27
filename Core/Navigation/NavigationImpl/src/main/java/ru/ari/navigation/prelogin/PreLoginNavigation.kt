package ru.ari.navigation.prelogin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.ari.composelib.LocalPreLoginNavigator
import ru.ari.navigation.BaseNavigatorImpl
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.navigation.rememberNavigationState
import ru.ari.navigation.toEntries

val PRE_LOGIN_ROUTES = persistentListOf(
    Route.PreLogin.LoginScreenRoute,
    Route.PreLogin.RegistrationScreenRoute
)

@Composable
fun PreLoginNavigation(
    preLoginRoutes: ImmutableList<RouteEntryProvider>,
    modifier: Modifier = Modifier
) {
    val preLoginNavigationState = rememberNavigationState(
        startRoute = Route.PreLogin.LoginScreenRoute,
        topLevelRoutes = PRE_LOGIN_ROUTES
    )
    val preLoginNavigator = remember(preLoginNavigationState) {
        BaseNavigatorImpl(preLoginNavigationState)
    }
    CompositionLocalProvider(
        LocalPreLoginNavigator provides preLoginNavigator
    ) {
        val entryProvider = entryProvider {
            preLoginRoutes.fastForEach { routeProvider ->
                with (routeProvider) {
                    provideRoute()
                }
            }
        }
        NavDisplay(
            entries = preLoginNavigationState.toEntries(entryProvider),
            modifier = modifier,
            onBack = preLoginNavigator::goBack
        )
    }
}