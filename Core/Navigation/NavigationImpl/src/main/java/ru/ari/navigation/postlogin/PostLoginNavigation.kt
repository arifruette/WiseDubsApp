package ru.ari.navigation.postlogin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.collections.immutable.persistentListOf
import ru.ari.navigation.Route
import ru.ari.navigation.common.Navigator
import ru.ari.navigation.common.rememberNavigationState
import ru.ari.navigation.common.toEntries
import ru.ari.sharing.navigation.provideSharingScreen

val POST_LOGIN_ROUTES = persistentListOf(
    Route.PostLogin.SharingScreenRoute
)

@Composable
fun PostLoginNavigation(
    modifier: Modifier = Modifier
) {
    val postLoginNavigationState = rememberNavigationState(
        startRoute = Route.PostLogin.SharingScreenRoute,
        topLevelRoutes = POST_LOGIN_ROUTES
    )
    val preLoginNavigator = remember {
        Navigator(postLoginNavigationState)
    }
    val entryProvider = entryProvider {
        provideSharingScreen()
    }
    NavDisplay(
        entries = postLoginNavigationState.toEntries(entryProvider),
        modifier = modifier,
        onBack = preLoginNavigator::goBack
    )
}