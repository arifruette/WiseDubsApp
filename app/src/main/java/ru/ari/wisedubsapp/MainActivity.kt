package ru.ari.wisedubsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.ari.navigation.NavigationRoot
import ru.ari.wisedubsapp.ui.theme.WiseDubsAppTheme

class MainActivity : ComponentActivity() {

    @Volatile
    private var isRouteAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        val appComponent = (applicationContext as WiseDubsApplication).appComponent
        super.onCreate(savedInstanceState)
        val mainViewModel: MainViewModel by viewModels {
            appComponent.mainViewModelFactory
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
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            NavigationRoot(
                                startRoute = (startRouteState as StartRouteState.Computed).route,
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