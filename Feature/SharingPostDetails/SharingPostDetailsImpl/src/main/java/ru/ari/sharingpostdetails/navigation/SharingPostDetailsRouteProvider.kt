package ru.ari.sharingpostdetails.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import ru.ari.cache.di.CacheApi
import ru.ari.composelib.LocalAppMessageHost
import ru.ari.composelib.LocalPostLoginNavigator
import ru.ari.composelib.daggerViewModel
import ru.ari.composelib.di.utils.rememberScopedComponent
import ru.ari.di.deps
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.posts.api.di.PostsApi
import ru.ari.sharingpostdetails.di.component.DaggerSharingPostDetailsComponent
import ru.ari.sharingpostdetails.presentation.contract.SharingPostDetailsUiAction
import ru.ari.sharingpostdetails.presentation.contract.SharingPostDetailsUiEffect
import ru.ari.sharingpostdetails.presentation.ui.SharingPostDetailsScreen
import ru.ari.sharingpostdetails.presentation.viewmodel.SharingPostDetailsViewModel

class SharingPostDetailsRouteProvider @Inject constructor() : RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PostLogin.SharingPostDetailsRoute> { route ->
            val context = LocalContext.current
            val component = rememberScopedComponent {
                context.run {
                    DaggerSharingPostDetailsComponent.factory().create(
                        postsApi = deps<PostsApi>(),
                        cacheApi = deps<CacheApi>()
                    )
                }
            }
            val viewModel = daggerViewModel<SharingPostDetailsViewModel> {
                component.sharingPostDetailsViewModelFactory
            }
            SharingPostDetailsNavigationRoute(
                viewModel = viewModel,
                postId = route.postId,
                autoReserve = route.autoReserve
            )
        }
    }
}

@Composable
private fun SharingPostDetailsNavigationRoute(
    viewModel: SharingPostDetailsViewModel,
    postId: Long,
    autoReserve: Boolean
) {
    val context = LocalContext.current
    val navigator = LocalPostLoginNavigator.current
    val appMessageHost = LocalAppMessageHost.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(postId, autoReserve, viewModel) {
        viewModel.onAction(
            SharingPostDetailsUiAction.Load(
                postId = postId,
                autoReserve = autoReserve
            )
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is SharingPostDetailsUiEffect.ShowMessage -> {
                    appMessageHost.showMessage(effect.message)
                }

                is SharingPostDetailsUiEffect.CloseWithError -> {
                    appMessageHost.showMessage(effect.message)
                    navigator.goBack()
                }

                is SharingPostDetailsUiEffect.OpenTelegram -> {
                    val opened = openTelegram(context = context, telegramId = effect.telegramId)
                    if (!opened) {
                        appMessageHost.showMessage("Не удалось открыть Telegram")
                    }
                }
            }
        }
    }

    SharingPostDetailsScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBackClick = navigator::goBack
    )
}

private fun openTelegram(context: android.content.Context, telegramId: String): Boolean {
    val cleanTelegramId = telegramId.trim().removePrefix("@")
    if (cleanTelegramId.isBlank()) {
        return false
    }

    val intent = Intent(
        Intent.ACTION_VIEW,
        "tg://resolve?domain=$cleanTelegramId".toUri()
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    return try {
        context.startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://t.me/$cleanTelegramId".toUri()
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            true
        } catch (_: Throwable) {
            false
        }
    }
}
