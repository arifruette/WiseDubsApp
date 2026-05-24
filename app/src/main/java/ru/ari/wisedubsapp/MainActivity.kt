package ru.ari.wisedubsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import ru.ari.composelib.AppMessageHost
import ru.ari.composelib.LocalAppMessageHost
import ru.ari.designsystem.theme.WiseDubsAppTheme
import ru.ari.designsystem.theme.setEdgeToEdgeConfig
import ru.ari.navigation.NavigationRoot
import ru.ari.wisedubsapp.presentation.viewmodel.MainViewModel
import ru.ari.wisedubsapp.presentation.viewmodel.contract.StartRouteState

class MainActivity : ComponentActivity() {

    @Volatile
    private var isRouteAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()

        val appComponent = (applicationContext as WiseDubsApplication).appComponent
        val preLoginRoutes = appComponent.preLoginRoutes.toImmutableList()
        val postLoginRoutes = appComponent.postLoginRoutes.toImmutableList()

        super.onCreate(savedInstanceState)
        val mainViewModel: MainViewModel by viewModels {
            appComponent.mainViewModelFactory
        }
        splash.setKeepOnScreenCondition {
            !isRouteAvailable
        }
        setEdgeToEdgeConfig()
        setContent {
            val startRouteState by mainViewModel.uiState.collectAsStateWithLifecycle()
            isRouteAvailable = remember(startRouteState) {
                startRouteState is StartRouteState.Computed
            }
            when (startRouteState) {
                is StartRouteState.Loading -> Unit
                is StartRouteState.Computed -> {
                    val computedState = startRouteState as StartRouteState.Computed
                    WiseDubsAppTheme {
                        val snackbarHostState = remember { SnackbarHostState() }
                        val appMessageScope = rememberCoroutineScope()
                        val appMessageHost = remember(snackbarHostState, appMessageScope) {
                            AppMessageHost { message ->
                                appMessageScope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        }
                        CompositionLocalProvider(
                            LocalAppMessageHost provides appMessageHost
                        ) {
                            NavigationRoot(
                                startRoute = computedState.route,
                                navigationResetKey = computedState.navigationResetKey,
                                preLoginRoutes = preLoginRoutes,
                                postLoginRoutes = postLoginRoutes,
                                snackbarHostState = snackbarHostState,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
