package ru.ari.navigation.prelogin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.collections.immutable.persistentListOf
import ru.ari.login.navigation.provideLoginScreen
import ru.ari.navigation.Route
import ru.ari.navigation.common.Navigator
import ru.ari.navigation.common.rememberNavigationState
import ru.ari.navigation.common.toEntries

val PRE_LOGIN_ROUTES = persistentListOf(
    Route.PreLogin.LoginScreenRoute
)

@Composable
fun PreLoginNavigation(
    modifier: Modifier = Modifier
) {
    val preLoginNavigationState = rememberNavigationState(
        startRoute = Route.PreLogin.LoginScreenRoute,
        topLevelRoutes = PRE_LOGIN_ROUTES
    )
    val preLoginNavigator = remember {
        Navigator(preLoginNavigationState)
    }
    val entryProvider = entryProvider {
        provideLoginScreen()
    }
    NavDisplay(
        entries = preLoginNavigationState.toEntries(entryProvider),
        modifier = modifier,
        onBack = preLoginNavigator::goBack
    )
}