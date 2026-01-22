package ru.ari.wisedubsapp.di.component

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.cache.di.CacheApi
import ru.ari.navigation.di.PostLoginRoutes
import ru.ari.navigation.di.PreLoginRoutes
import ru.ari.navigation.di.RouteEntryProvider
import ru.ari.wisedubsapp.di.modules.MainViewModelFactoryModule
import ru.ari.wisedubsapp.di.modules.NavigationEntriesModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NavigationEntriesModule::class,
        MainViewModelFactoryModule::class
    ],
    dependencies = [CacheApi::class]
)
interface AppComponent {
    val mainViewModelFactory: ViewModelProvider.Factory

    @get:PreLoginRoutes
    val preLoginRoutes: Set<RouteEntryProvider>

    @get:PostLoginRoutes
    val postLoginRoutes: Set<RouteEntryProvider>

    @Component.Factory
    interface Factory {
        fun create(
            cacheApi: CacheApi
        ): AppComponent
    }
}