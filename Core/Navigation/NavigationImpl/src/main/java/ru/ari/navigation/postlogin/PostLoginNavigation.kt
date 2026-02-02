package ru.ari.navigation.postlogin

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.util.fastForEach
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.ari.composelib.LocalPostLoginNavigator
import ru.ari.navigation.BaseNavigatorImpl
import ru.ari.navigation.R
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.navigation.postlogin.model.NavBarItem
import ru.ari.navigation.rememberNavigationState
import ru.ari.navigation.toEntries

val POST_LOGIN_ROUTES = persistentListOf(
    Route.PostLogin.SharingScreenRoute,
    Route.PostLogin.PostsScreenRoute,
    Route.PostLogin.ProfileScreenRoute
)

private val POST_LOGIN_TOP_LEVEL_ROUTES = mapOf(
    Route.PostLogin.SharingScreenRoute to NavBarItem(
        iconRes = R.drawable.menu,
        label = "Sharing"
    ),
    Route.PostLogin.PostsScreenRoute to NavBarItem(
        iconRes = R.drawable.post,
        label = "Posts"
    ),
    Route.PostLogin.ProfileScreenRoute to NavBarItem(
        iconRes = R.drawable.account,
        label = "Profile"
    )
)

@Composable
fun PostLoginNavigation(
    postLoginRoutes: ImmutableList<RouteEntryProvider>,
    modifier: Modifier = Modifier
) {
    val postLoginNavigationState = rememberNavigationState(
        startRoute = Route.PostLogin.SharingScreenRoute,
        topLevelRoutes = POST_LOGIN_ROUTES
    )
    val postLoginNavigator = remember(postLoginNavigationState) {
        BaseNavigatorImpl(postLoginNavigationState)
    }
    CompositionLocalProvider(
        LocalPostLoginNavigator provides postLoginNavigator
    ) {
        val entryProvider = entryProvider {
            entry<Route.PostLogin.PostsScreenRoute> {
                Text(text = "Posts Screen")
            }
            entry<Route.PostLogin.ProfileScreenRoute> {
                Text(text = "Profile Screen")
            }
            postLoginRoutes.fastForEach { routeProvider ->
                with(routeProvider) {
                    provideRoute()
                }
            }
        }
        Scaffold(
            modifier = modifier,
            bottomBar = {
                NavigationBar {
                    POST_LOGIN_TOP_LEVEL_ROUTES.forEach { (key, value) ->
                        val isSelected = key == postLoginNavigationState.topLevelRoute
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { postLoginNavigator.navigate(key) },
                            icon = {
                                Icon(
                                    painter = painterResource(id = value.iconRes),
                                    contentDescription = value.label
                                )
                            },
                            label = { Text(value.label) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavDisplay(
                entries = postLoginNavigationState.toEntries(entryProvider),
                modifier = Modifier.padding(innerPadding),
                onBack = postLoginNavigator::goBack
            )
        }
    }
}