package ru.ari.booking.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import ru.ari.booking.di.component.DaggerBookingComponent
import ru.ari.booking.presentation.contract.BookingActionHandler
import ru.ari.booking.presentation.contract.BookingScreenAction
import ru.ari.booking.presentation.contract.BookingScreenUiEffect
import ru.ari.booking.presentation.ui.BookingScreen
import ru.ari.booking.presentation.viewmodel.BookingViewModel
import ru.ari.composelib.LocalAppMessageHost
import ru.ari.composelib.daggerViewModel
import ru.ari.composelib.di.utils.rememberScopedComponent
import ru.ari.di.deps
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.network.di.NetworkApi

class BookingScreenRouteProvider @Inject constructor() : RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PostLogin.BookingScreenRoute> {
            val context = LocalContext.current
            val component = rememberScopedComponent {
                context.run {
                    DaggerBookingComponent.factory().create(
                        networkApi = deps<NetworkApi>()
                    )
                }
            }
            val viewModel = daggerViewModel<BookingViewModel> {
                component.bookingViewModelFactory
            }
            BookingScreenNavigationRoute(viewModel = viewModel)
        }
    }
}

@Composable
private fun BookingScreenNavigationRoute(
    viewModel: BookingViewModel
) {
    val appMessageHost = LocalAppMessageHost.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var scrollToBookingsRequest by remember { mutableIntStateOf(0) }
    val actionHandler = remember(viewModel) {
        BookingActionHandler(viewModel::onAction)
    }

    LaunchedEffect(viewModel) {
        viewModel.onAction(BookingScreenAction.Load)
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is BookingScreenUiEffect.ShowMessage -> appMessageHost.showMessage(effect.message)
                BookingScreenUiEffect.ScrollToBookings -> scrollToBookingsRequest++
            }
        }
    }

    BookingScreen(
        uiState = uiState,
        actionHandler = actionHandler,
        scrollToBookingsRequest = scrollToBookingsRequest
    )
}
