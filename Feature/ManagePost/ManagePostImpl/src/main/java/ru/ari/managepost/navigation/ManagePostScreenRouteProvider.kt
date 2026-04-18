package ru.ari.managepost.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.ari.composelib.LocalPostLoginNavigator
import ru.ari.composelib.daggerViewModel
import ru.ari.composelib.di.utils.rememberScopedComponent
import ru.ari.di.deps
import ru.ari.managepost.di.component.DaggerManagePostComponent
import ru.ari.managepost.presentation.contract.ManagePostActionHandler
import ru.ari.managepost.presentation.contract.ManagePostScreenAction
import ru.ari.managepost.presentation.contract.ManagePostScreenUiEffect
import ru.ari.managepost.presentation.models.ManagePostMode
import ru.ari.managepost.presentation.ui.ManagePostScreen
import ru.ari.managepost.presentation.viewmodel.ManagePostViewModel
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.posts.api.di.PostsApi
import javax.inject.Inject

class ManagePostScreenRouteProvider @Inject constructor() : RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PostLogin.CreatePostScreenRoute> {
            val context = LocalContext.current
            val component = rememberScopedComponent {
                context.run {
                    DaggerManagePostComponent.factory().create(deps<PostsApi>())
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
                    DaggerManagePostComponent.factory().create(deps<PostsApi>())
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
    }
}

@Composable
private fun ManagePostNavigationRoute(
    viewModel: ManagePostViewModel,
    mode: ManagePostMode
) {
    val context = LocalContext.current
    val navigator = LocalPostLoginNavigator.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionHandler = remember(viewModel) {
        ManagePostActionHandler(viewModel::onAction)
    }

    LaunchedEffect(mode) {
        viewModel.onAction(ManagePostScreenAction.Load(mode))
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ManagePostScreenUiEffect.Completed -> navigator.goBack()
                is ManagePostScreenUiEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
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
