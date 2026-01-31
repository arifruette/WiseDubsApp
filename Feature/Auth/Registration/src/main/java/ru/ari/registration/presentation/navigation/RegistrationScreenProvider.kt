package ru.ari.registration.presentation.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.ari.composelib.LocalPreLoginNavigator
import ru.ari.composelib.LocalRootNavigator
import ru.ari.composelib.daggerViewModel
import ru.ari.composelib.di.utils.rememberScopedComponent
import ru.ari.di.deps
import ru.ari.navigation.Route
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.registration.di.component.DaggerRegistrationComponent
import ru.ari.registration.presentation.contract.RegistrationScreenUiEffect
import ru.ari.registration.presentation.ui.RegistrationScreen
import ru.ari.registration.presentation.viewmodel.RegistrationViewModel
import javax.inject.Inject

class RegistrationScreenProvider @Inject constructor(): RouteEntryProvider {
    override fun EntryProviderScope<NavKey>.provideRoute() {
        entry<Route.PreLogin.RegistrationScreenRoute> {
            val context = LocalContext.current
            val component = rememberScopedComponent {
                context.run {
                    DaggerRegistrationComponent.factory().create(deps())
                }
            }
            val registrationViewModel = daggerViewModel<RegistrationViewModel> {
                component.registrationViewModelFactory
            }
            RegistrationScreenRoute(registrationViewModel)
        }
    }
}

@Composable
private fun RegistrationScreenRoute(
    viewModel: RegistrationViewModel
) {
    val uiEffect = viewModel.uiEffect
    val context = LocalContext.current

    val rootNavigator = LocalRootNavigator.current
    val authNavigator = LocalPreLoginNavigator.current

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is RegistrationScreenUiEffect.ShowError -> Toast.makeText(
                    context,
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()

                is RegistrationScreenUiEffect.NavigateToMainScreen -> {
                    rootNavigator.navigate(Route.PostLogin)
                }

                is RegistrationScreenUiEffect.NavigateToLoginScreen -> {
                    authNavigator.navigate(Route.PreLogin.LoginScreenRoute)
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegistrationScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}
