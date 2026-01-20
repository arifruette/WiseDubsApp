package ru.ari.wisedubsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.toImmutableList
import ru.ari.composelib.LocalViewModelProvider
import ru.ari.navigation.NavigationRoot
import ru.ari.designsystem.theme.WiseDubsAppTheme

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
            appComponent.compositeViewModelProvider
        }
        splash.setKeepOnScreenCondition {
            !isRouteAvailable
        }
        enableEdgeToEdge()
        setContent {
            val startRouteState by mainViewModel.uiState.collectAsStateWithLifecycle()
            isRouteAvailable = remember(startRouteState) {
                startRouteState is StartRouteState.Computed
            }
            when (startRouteState) {
                is StartRouteState.Loading -> Unit
                is StartRouteState.Computed -> {
                    WiseDubsAppTheme {
                        CompositionLocalProvider(LocalViewModelProvider provides appComponent.compositeViewModelProvider) {
                            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                                NavigationRoot(
                                    startRoute = (startRouteState as StartRouteState.Computed).route,
                                    preLoginRoutes = preLoginRoutes,
                                    postLoginRoutes = postLoginRoutes,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}