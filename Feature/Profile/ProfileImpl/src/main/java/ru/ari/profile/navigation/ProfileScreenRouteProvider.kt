package ru.ari.profile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import ru.ari.composelib.LocalAppMessageHost
import ru.ari.auth.common.api.di.AuthCommonApi
import ru.ari.composelib.LocalPostLoginNavigator
import ru.ari.composelib.daggerViewModel
import ru.ari.composelib.di.utils.rememberScopedComponent
import ru.ari.di.deps
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.posts.api.di.PostsApi
import ru.ari.profile.di.component.DaggerProfileComponent
import ru.ari.profile.di.component.ProfileComponent
import ru.ari.profile.presentation.contract.ProfileScreenUiEffect
import ru.ari.profile.presentation.contract.ReservedPostsScreenAction
import ru.ari.profile.presentation.contract.ReservedPostsScreenUiEffect
import ru.ari.profile.presentation.ui.ProfileScreen
import ru.ari.profile.presentation.ui.ReservedPostsScreen
import ru.ari.profile.presentation.viewmodel.ProfileViewModel
import ru.ari.profile.presentation.viewmodel.ReservedPostsViewModel
import ru.ari.sharingpostdetails.api.di.SharingPostDetailsApi

class ProfileScreenRouteProvider @Inject constructor() : RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PostLogin.ProfileScreenRoute> {
            val component = rememberProfileComponent()
            val viewModel = daggerViewModel<ProfileViewModel> {
                component.profileViewModelFactory
            }
            ProfileScreenNavigationRoute(viewModel = viewModel)
        }

        entry<Route.PostLogin.ReservedPostsScreenRoute> {
            val component = rememberProfileComponent()
            val viewModel = daggerViewModel<ReservedPostsViewModel> {
                component.reservedPostsViewModelFactory
            }
            ReservedPostsScreenNavigationRoute(viewModel = viewModel)
        }
    }
}

@Composable
private fun rememberProfileComponent(): ProfileComponent {
    val context = LocalContext.current
    return rememberScopedComponent {
        context.run {
            DaggerProfileComponent.factory().create(
                authCommonApi = deps<AuthCommonApi>(),
                postsApi = deps<PostsApi>(),
                sharingPostDetailsApi = deps<SharingPostDetailsApi>()
            )
        }
    }
}

@Composable
private fun ProfileScreenNavigationRoute(
    viewModel: ProfileViewModel
) {
    val appMessageHost = LocalAppMessageHost.current
    val postLoginNavigator = LocalPostLoginNavigator.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ProfileScreenUiEffect.OpenReservedPosts -> {
                    postLoginNavigator.navigate(Route.PostLogin.ReservedPostsScreenRoute)
                }

                ProfileScreenUiEffect.OpenMyBookings -> {
                    postLoginNavigator.navigate(Route.PostLogin.MyBookingsRoute)
                }

                is ProfileScreenUiEffect.ShowError -> {
                    appMessageHost.showMessage(effect.message)
                }
            }
        }
    }

    ProfileScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ReservedPostsScreenNavigationRoute(
    viewModel: ReservedPostsViewModel
) {
    val context = LocalContext.current
    val postLoginNavigator = LocalPostLoginNavigator.current
    val detailsLauncher = context.deps<SharingPostDetailsApi>().sharingPostDetailsLauncher
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.onAction(ReservedPostsScreenAction.Load)
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ReservedPostsScreenUiEffect.NavigateBack -> {
                    postLoginNavigator.goBack()
                }

                ReservedPostsScreenUiEffect.OpenSharing -> {
                    postLoginNavigator.navigate(Route.PostLogin.SharingScreenRoute)
                }

                is ReservedPostsScreenUiEffect.OpenPostDetails -> {
                    detailsLauncher.open(
                        navigator = postLoginNavigator,
                        postId = effect.postId,
                        autoReserve = false
                    )
                }
            }
        }
    }

    ReservedPostsScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}
