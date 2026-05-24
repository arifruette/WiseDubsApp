package ru.ari.login.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.ari.composelib.LocalAppMessageHost
import ru.ari.composelib.LocalPreLoginNavigator
import ru.ari.composelib.daggerViewModel
import ru.ari.composelib.di.utils.rememberScopedComponent
import ru.ari.di.deps
import ru.ari.login.di.component.DaggerLoginComponent
import ru.ari.login.presentation.contract.LoginScreenUiEffect
import ru.ari.login.presentation.ui.LoginScreen
import ru.ari.login.presentation.viewmodel.LoginViewModel
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import javax.inject.Inject

class LoginScreenProvider @Inject constructor() : RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PreLogin.LoginScreenRoute> {
            val context = LocalContext.current
            val component = rememberScopedComponent {
                context.run {
                    DaggerLoginComponent.factory().create(deps())
                }
            }
            val loginViewModel = daggerViewModel<LoginViewModel> {
                component.loginViewModelFactory
            }
            LoginScreenNavigationRoute(loginViewModel)
        }
    }
}

@Composable
private fun LoginScreenNavigationRoute(
    loginViewModel: LoginViewModel
) {
    val authNavigator = LocalPreLoginNavigator.current
    val appMessageHost = LocalAppMessageHost.current

    LaunchedEffect(Unit) {
        loginViewModel.uiEffect.collect { effect ->
            when (effect) {
                is LoginScreenUiEffect.ShowError -> appMessageHost.showMessage(effect.message)

                is LoginScreenUiEffect.NavigateToMainScreen -> Unit

                is LoginScreenUiEffect.NavigateToRegistrationScreen -> {
                    authNavigator.navigate(Route.PreLogin.RegistrationScreenRoute)
                }
            }
        }
    }

    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

    LoginScreen(
        uiState = uiState,
        onAction = loginViewModel::onAction
    )
}
