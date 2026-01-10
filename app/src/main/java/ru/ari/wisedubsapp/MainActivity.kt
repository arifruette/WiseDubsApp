package ru.ari.wisedubsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import ru.ari.navigation.NavigationRoot
import ru.ari.navigation.Route
import ru.ari.wisedubsapp.ui.theme.WiseDubsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val appComponent = (applicationContext as WiseDubsApplication).appComponent
        super.onCreate(savedInstanceState)
        val mainViewModel: MainViewModel by viewModels {
            appComponent.mainViewModelFactory
        }
        enableEdgeToEdge()
        setContent {
            WiseDubsAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationRoot(
                        startRoute = Route.PostLogin,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}