package ru.ari.sharing.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import ru.ari.cache.di.CacheApi
import ru.ari.composelib.LocalPostLoginNavigator
import ru.ari.composelib.daggerViewModel
import ru.ari.composelib.di.utils.rememberScopedComponent
import ru.ari.di.deps
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.posts.api.di.PostsApi
import ru.ari.sharing.di.component.DaggerSharingComponent
import ru.ari.sharing.presentation.contract.SharingScreenAction
import ru.ari.sharing.presentation.contract.SharingScreenUiEffect
import ru.ari.sharing.presentation.ui.SharingScreen
import ru.ari.sharing.presentation.viewmodel.SharingViewModel
import ru.ari.sharingpostdetails.api.di.SharingPostDetailsApi

class SharingScreenRouteProvider @Inject constructor() : RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PostLogin.SharingScreenRoute> {
            val context = LocalContext.current
            val component = rememberScopedComponent {
                context.run {
                    DaggerSharingComponent.factory().create(
                        postsApi = deps<PostsApi>(),
                        cacheApi = deps<CacheApi>()
                    )
                }
            }
            val sharingViewModel = daggerViewModel<SharingViewModel> {
                component.sharingViewModelFactory
            }
            SharingScreenRoute(viewModel = sharingViewModel)
        }
    }
}

@Composable
private fun SharingScreenRoute(
    viewModel: SharingViewModel
) {
    val context = LocalContext.current
    val navigator = LocalPostLoginNavigator.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val sharingPostDetailsLauncher = remember(context) {
        context.deps<SharingPostDetailsApi>().sharingPostDetailsLauncher
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner, viewModel) {
        var isFirstResume = true
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onAction(
                    if (isFirstResume) {
                        isFirstResume = false
                        SharingScreenAction.LoadPosts
                    } else {
                        SharingScreenAction.SilentRefreshPosts
                    }
                )
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
                is SharingScreenUiEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is SharingScreenUiEffect.NavigateToDetails -> {
                    sharingPostDetailsLauncher.open(
                        navigator = navigator,
                        postId = effect.postId,
                        autoReserve = effect.autoReserve
                    )
                }
            }
        }
    }

    SharingScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}
