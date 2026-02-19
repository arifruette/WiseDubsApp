package ru.ari.sharing.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.ari.cache.di.CacheApi
import ru.ari.composelib.daggerViewModel
import ru.ari.composelib.di.utils.rememberScopedComponent
import ru.ari.di.deps
import ru.ari.network.di.NetworkApi
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.sharing.di.component.DaggerSharingComponent
import ru.ari.sharing.presentation.contract.SharingScreenAction
import ru.ari.sharing.presentation.contract.SharingScreenUiEffect
import ru.ari.sharing.presentation.ui.SharingScreen
import ru.ari.sharing.presentation.viewmodel.SharingViewModel
import javax.inject.Inject

class SharingScreenRouteProvider @Inject constructor() : RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PostLogin.SharingScreenRoute> {
            val context = LocalContext.current
            val component = rememberScopedComponent {
                context.run {
                    DaggerSharingComponent.factory().create(
                        deps<NetworkApi>(),
                        deps<CacheApi>()
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onAction(SharingScreenAction.LoadPosts)
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is SharingScreenUiEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is SharingScreenUiEffect.NavigateToDetails -> {
                    Toast.makeText(
                        context,
                        "Экран деталей пока не реализован, id=${effect.postId}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    SharingScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}
