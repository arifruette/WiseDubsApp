package ru.ari.wisedubsapp.di

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.cache.di.DataStoreDeps
import ru.ari.di.viewmodel.CoreModule
import ru.ari.navigation.NavigationEntriesModule
import ru.ari.navigation.di.PostLoginRoutes
import ru.ari.navigation.di.PreLoginRoutes
import ru.ari.navigation.di.RouteEntryProvider
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        CoreModule::class,
        NavigationEntriesModule::class,
    ],
    dependencies = [DataStoreDeps::class]
)
interface AppComponent {
    val compositeViewModelProvider: ViewModelProvider.Factory

    @get:PreLoginRoutes
    val preLoginRoutes: Set<RouteEntryProvider>

    @get:PostLoginRoutes
    val postLoginRoutes: Set<RouteEntryProvider>

    @Component.Factory
    interface Factory {
        fun create(
            dataStoreDeps: DataStoreDeps
        ): AppComponent
    }
}