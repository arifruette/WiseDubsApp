package ru.ari.wisedubsapp.di

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.cache.di.DataStoreDeps
import ru.ari.di.viewmodel.CoreModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        CoreModule::class
    ],
    dependencies = [DataStoreDeps::class]
)
interface AppComponent {
    val mainViewModelFactory: ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            dataStoreDeps: DataStoreDeps
        ): AppComponent
    }
}