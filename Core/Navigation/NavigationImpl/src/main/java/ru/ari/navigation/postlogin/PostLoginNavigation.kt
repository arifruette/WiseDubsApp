package ru.ari.navigation.postlogin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.collections.immutable.persistentListOf
import ru.ari.navigation.BaseNavigatorImpl
import ru.ari.navigation.LocalPostLoginNavigator
import ru.ari.navigation.Route
import ru.ari.navigation.rememberNavigationState
import ru.ari.navigation.toEntries
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
    val postLoginNavigator = remember {
        BaseNavigatorImpl(postLoginNavigationState)
    }
    CompositionLocalProvider(
        LocalPostLoginNavigator provides postLoginNavigator
    ) {
        val entryProvider = entryProvider {
            provideSharingScreen()
        }
        NavDisplay(
            entries = postLoginNavigationState.toEntries(entryProvider),
            modifier = modifier,
            onBack = postLoginNavigator::goBack
        )
    }
}