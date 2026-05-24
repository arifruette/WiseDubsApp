package ru.ari.managepost.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import ru.ari.composelib.LocalAppMessageHost
import ru.ari.composelib.LocalPostLoginNavigator
import ru.ari.composelib.daggerViewModel
import ru.ari.composelib.di.utils.rememberScopedComponent
import ru.ari.di.deps
import ru.ari.managepost.di.component.DaggerManagePostComponent
import ru.ari.managepost.presentation.address.contract.AddressManageAction
import ru.ari.managepost.presentation.address.contract.AddressManageActionHandler
import ru.ari.managepost.presentation.address.contract.AddressManageEffect
import ru.ari.managepost.presentation.address.ui.AddressManageScreen
import ru.ari.managepost.presentation.address.viewmodel.AddressManageViewModel
import ru.ari.managepost.presentation.contract.ManagePostActionHandler
import ru.ari.managepost.presentation.contract.ManagePostScreenAction
import ru.ari.managepost.presentation.contract.ManagePostScreenUiEffect
import ru.ari.managepost.presentation.models.ManagePostMode
import ru.ari.managepost.presentation.ui.ManagePostScreen
import ru.ari.managepost.presentation.viewmodel.ManagePostViewModel
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.network.di.NetworkApi
import ru.ari.posts.api.di.PostsApi

class ManagePostScreenRouteProvider @Inject constructor() : RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PostLogin.CreatePostScreenRoute> {
            val context = LocalContext.current
            val component = rememberScopedComponent {
                context.run {
                    DaggerManagePostComponent.factory().create(
                        postsApi = deps<PostsApi>(),
                        networkApi = deps<NetworkApi>()
                    )
                }
            }
            val viewModel = daggerViewModel<ManagePostViewModel> {
                component.managePostViewModelFactory
            }
            ManagePostNavigationRoute(viewModel = viewModel, mode = ManagePostMode.Create)
        }

        entry<Route.PostLogin.EditPostScreenRoute> { route ->
            val context = LocalContext.current
            val component = rememberScopedComponent {
                context.run {
                    DaggerManagePostComponent.factory().create(
                        postsApi = deps<PostsApi>(),
                        networkApi = deps<NetworkApi>()
                    )
                }
            }
            val viewModel = daggerViewModel<ManagePostViewModel> {
                component.managePostViewModelFactory
            }
            ManagePostNavigationRoute(
                viewModel = viewModel,
                mode = ManagePostMode.Edit(route.postId)
            )
        }

        entry<Route.PostLogin.AddressManageScreenRoute> { route ->
            val context = LocalContext.current
            val component = rememberScopedComponent {
                context.run {
                    DaggerManagePostComponent.factory().create(
                        postsApi = deps<PostsApi>(),
                        networkApi = deps<NetworkApi>()
                    )
                }
            }
            val viewModel = daggerViewModel<AddressManageViewModel> {
                component.managePostViewModelFactory
            }
            AddressManageNavigationRoute(viewModel = viewModel, locationId = route.locationId)
        }
    }
}

@Composable
private fun ManagePostNavigationRoute(
    viewModel: ManagePostViewModel,
    mode: ManagePostMode
) {
    val appMessageHost = LocalAppMessageHost.current
    val navigator = LocalPostLoginNavigator.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(mode, viewModel) {
        viewModel.prepareForMode(mode)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionHandler = remember(viewModel) {
        ManagePostActionHandler(viewModel::onAction)
    }

    DisposableEffect(lifecycleOwner, viewModel, mode) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onAction(ManagePostScreenAction.Load(mode))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ManagePostScreenUiEffect.Completed -> {
                    if (effect.isCreate) {
                        navigator.replaceStack(Route.PostLogin.BookingScreenRoute)
                    } else {
                        navigator.goBack()
                    }
                }
                is ManagePostScreenUiEffect.ShowError -> {
                    appMessageHost.showMessage(effect.message)
                }

                is ManagePostScreenUiEffect.NavigateToAddressManage -> {
                    navigator.navigate(Route.PostLogin.AddressManageScreenRoute(effect.locationId))
                }
            }
        }
    }

    ManagePostScreen(
        uiState = uiState,
        actionHandler = actionHandler,
        onBackClick = navigator::goBack
    )
}

@Composable
private fun AddressManageNavigationRoute(
    viewModel: AddressManageViewModel,
    locationId: Int?
) {
    val appMessageHost = LocalAppMessageHost.current
    val navigator = LocalPostLoginNavigator.current

    LaunchedEffect(locationId, viewModel) {
        viewModel.prepareForRoute(locationId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val actionHandler = remember(viewModel) {
        object : AddressManageActionHandler {
            override fun onAction(action: AddressManageAction) {
                viewModel.onAction(action)
            }
        }
    }

    LaunchedEffect(locationId) {
        viewModel.onAction(AddressManageAction.Load(locationId))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddressManageEffect.Back -> navigator.goBack()
                is AddressManageEffect.ShowError -> {
                    appMessageHost.showMessage(effect.message)
                }
            }
        }
    }

    AddressManageScreen(
        state = state,
        actionHandler = actionHandler,
        onBackClick = navigator::goBack,
        locationId = locationId
    )
}
