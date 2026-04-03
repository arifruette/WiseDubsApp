package ru.ari.myposts.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
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
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionHandler = remember(viewModel) {
        MyPostsActionHandler(viewModel::onAction)
    }

    LaunchedEffect(Unit) {
        viewModel.onAction(MyPostsScreenAction.Load)
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is MyPostsScreenUiEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                MyPostsScreenUiEffect.OpenCreatePost -> {
                    Toast.makeText(context, "Post form route is not connected yet", Toast.LENGTH_SHORT)
                        .show()
                }

                is MyPostsScreenUiEffect.OpenEditPost -> {
                    Toast.makeText(
                        context,
                        "Post form route is not connected yet, id=${effect.postId}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    MyPostsScreen(
        uiState = uiState,
        actionHandler = actionHandler
    )
}
