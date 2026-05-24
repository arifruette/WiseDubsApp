package ru.ari.myposts.navigation

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
import ru.ari.composelib.LocalAppMessageHost
import ru.ari.composelib.LocalPostLoginNavigator
import ru.ari.composelib.daggerViewModel
import ru.ari.composelib.di.utils.rememberScopedComponent
import ru.ari.di.deps
import ru.ari.myposts.di.component.DaggerMyPostsComponent
import ru.ari.myposts.presentation.contract.MyPostsActionHandler
import ru.ari.myposts.presentation.contract.MyPostsScreenAction
import ru.ari.myposts.presentation.contract.MyPostsScreenUiEffect
import ru.ari.myposts.presentation.ui.MyPostsScreen
import ru.ari.myposts.presentation.viewmodel.MyPostsViewModel
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.posts.api.di.PostsApi
import javax.inject.Inject

class MyPostsScreenRouteProvider @Inject constructor() : RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PostLogin.PostsScreenRoute> {
            val context = LocalContext.current
            val component = rememberScopedComponent {
                context.run {
                    DaggerMyPostsComponent.factory().create(deps<PostsApi>())
                }
            }
            val viewModel = daggerViewModel<MyPostsViewModel> {
                component.myPostsViewModelFactory
            }
            MyPostsScreenRoute(viewModel = viewModel)
        }
    }
}

@Composable
private fun MyPostsScreenRoute(
    viewModel: MyPostsViewModel
) {
    val appMessageHost = LocalAppMessageHost.current
    val navigator = LocalPostLoginNavigator.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionHandler = remember(viewModel) {
        MyPostsActionHandler(viewModel::onAction)
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onAction(MyPostsScreenAction.Load)
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
                is MyPostsScreenUiEffect.ShowError -> {
                    appMessageHost.showMessage(effect.message)
                }

                MyPostsScreenUiEffect.OpenCreatePost -> {
                    navigator.navigate(Route.PostLogin.CreatePostScreenRoute)
                }

                is MyPostsScreenUiEffect.OpenEditPost -> {
                    navigator.navigate(Route.PostLogin.EditPostScreenRoute(effect.postId))
                }
            }
        }
    }

    MyPostsScreen(
        uiState = uiState,
        actionHandler = actionHandler
    )
}
