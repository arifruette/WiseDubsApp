package ru.ari.booking.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import ru.ari.booking.di.component.DaggerBookingComponent
import ru.ari.booking.presentation.contract.BookingActionHandler
import ru.ari.booking.presentation.contract.BookingFormAction
import ru.ari.booking.presentation.contract.BookingFormActionHandler
import ru.ari.booking.presentation.contract.BookingFormUiEffect
import ru.ari.booking.presentation.contract.BookingScreenAction
import ru.ari.booking.presentation.contract.BookingScreenUiEffect
import ru.ari.booking.presentation.contract.MyBookingsAction
import ru.ari.booking.presentation.contract.MyBookingsUiEffect
import ru.ari.booking.presentation.ui.BookingFormScreen
import ru.ari.booking.presentation.ui.BookingScreen
import ru.ari.booking.presentation.ui.MyBookingsScreen
import ru.ari.booking.presentation.viewmodel.BookingFormViewModel
import ru.ari.booking.presentation.viewmodel.BookingViewModel
import ru.ari.booking.presentation.viewmodel.MyBookingsViewModel
import ru.ari.cache.di.CacheApi
import ru.ari.composelib.LocalAppMessageHost
import ru.ari.composelib.LocalPostLoginNavigator
import ru.ari.composelib.daggerViewModel
import ru.ari.composelib.di.utils.rememberScopedComponent
import ru.ari.di.deps
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.network.di.NetworkApi

class BookingScreenRouteProvider @Inject constructor() : RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PostLogin.BookingScreenRoute> {
            val component = rememberBookingComponent()
            val viewModel = daggerViewModel<BookingViewModel> {
                component.bookingViewModelFactory
            }
            BookingScreenNavigationRoute(viewModel = viewModel)
        }

        entry<Route.PostLogin.BookingFormRoute> { route ->
            val component = rememberBookingComponent()
            val viewModel = daggerViewModel<BookingFormViewModel> {
                component.bookingViewModelFactory
            }
            BookingFormNavigationRoute(viewModel = viewModel, route = route)
        }

        entry<Route.PostLogin.MyBookingsRoute> {
            val component = rememberBookingComponent()
            val viewModel = daggerViewModel<MyBookingsViewModel> {
                component.bookingViewModelFactory
            }
            MyBookingsNavigationRoute(viewModel = viewModel)
        }
    }
}

@Composable
private fun rememberBookingComponent() = LocalContext.current.let { context ->
    rememberScopedComponent {
        context.run {
            DaggerBookingComponent.factory().create(
                networkApi = deps<NetworkApi>(),
                cacheApi = deps<CacheApi>()
            )
        }
    }
}

@Composable
private fun BookingScreenNavigationRoute(
    viewModel: BookingViewModel
) {
    val appMessageHost = LocalAppMessageHost.current
    val postLoginNavigator = LocalPostLoginNavigator.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionHandler = remember(viewModel) { BookingActionHandler(viewModel::onAction) }

    LaunchedEffect(viewModel) {
        viewModel.onAction(BookingScreenAction.Load)
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is BookingScreenUiEffect.ShowMessage -> appMessageHost.showMessage(effect.message)
                is BookingScreenUiEffect.OpenBookingForm -> {
                    postLoginNavigator.navigate(
                        Route.PostLogin.BookingFormRoute(
                            initialRoomId = effect.roomId,
                            initialDate = effect.date
                        )
                    )
                }
                is BookingScreenUiEffect.OpenEditBooking -> {
                    postLoginNavigator.navigate(Route.PostLogin.BookingFormRoute(postId = effect.bookingId))
                }
            }
        }
    }

    BookingScreen(
        uiState = uiState,
        actionHandler = actionHandler
    )
}

@Composable
private fun BookingFormNavigationRoute(
    viewModel: BookingFormViewModel,
    route: Route.PostLogin.BookingFormRoute
) {
    val appMessageHost = LocalAppMessageHost.current
    val postLoginNavigator = LocalPostLoginNavigator.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionHandler = remember(viewModel) { BookingFormActionHandler(viewModel::onAction) }

    LaunchedEffect(viewModel, route) {
        viewModel.onAction(
            BookingFormAction.Load(
                postId = route.postId,
                initialRoomId = route.initialRoomId,
                initialDate = route.initialDate
            )
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                BookingFormUiEffect.NavigateBack -> postLoginNavigator.goBack()
                is BookingFormUiEffect.Completed -> {
                    if (effect.isCreate) {
                        postLoginNavigator.replaceStack(Route.PostLogin.BookingScreenRoute)
                    } else {
                        postLoginNavigator.goBack()
                    }
                    appMessageHost.showMessage(effect.message)
                }
                is BookingFormUiEffect.ShowMessage -> appMessageHost.showMessage(effect.message)
            }
        }
    }

    BookingFormScreen(uiState = uiState, actionHandler = actionHandler)
}

@Composable
private fun MyBookingsNavigationRoute(
    viewModel: MyBookingsViewModel
) {
    val appMessageHost = LocalAppMessageHost.current
    val postLoginNavigator = LocalPostLoginNavigator.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.onAction(MyBookingsAction.Load)
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                MyBookingsUiEffect.NavigateBack -> postLoginNavigator.goBack()
                is MyBookingsUiEffect.OpenEditBooking -> {
                    postLoginNavigator.navigate(Route.PostLogin.BookingFormRoute(postId = effect.bookingId))
                }
                is MyBookingsUiEffect.ShowMessage -> appMessageHost.showMessage(effect.message)
            }
        }
    }

    MyBookingsScreen(uiState = uiState, onAction = viewModel::onAction)
}
