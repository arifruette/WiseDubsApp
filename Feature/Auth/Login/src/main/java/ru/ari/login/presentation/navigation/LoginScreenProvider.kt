package ru.ari.login.presentation.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.ari.composelib.LocalRootNavigator
import ru.ari.composelib.daggerViewModel
import ru.ari.di.deps
import ru.ari.login.di.component.DaggerLoginComponent
import ru.ari.login.presentation.ui.LoginScreen
import ru.ari.login.presentation.viewmodel.LoginViewModel
import ru.ari.login.presentation.viewmodel.contract.LoginScreenUiEffect
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import javax.inject.Inject

class LoginScreenProvider @Inject constructor() : RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PreLogin.LoginScreenRoute> {
            val viewModelStoreOwner = LocalViewModelStoreOwner.current
            val context = LocalContext.current
            val component = rememberSaveable(viewModelStoreOwner) {
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
    val rootNavigator = LocalRootNavigator.current

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        loginViewModel.uiEffect.collect { effect ->
            when (effect) {
                is LoginScreenUiEffect.ShowError -> Toast.makeText(
                    context,
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()

                is LoginScreenUiEffect.NavigateToMainScreen -> {
                    rootNavigator.navigate(Route.PostLogin)
                }
            }
        }
    }

    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

    LoginScreen(
        uiState = uiState,
        onAction = loginViewModel::onAction,
        navigateToRegistrationScreen = {
            // TODO: поменять когда добавится экран реги
            rootNavigator.navigate(Route.PreLogin)
        }
    )
}