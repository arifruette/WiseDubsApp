package ru.ari.wisedubsapp.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.ari.wisedubsapp.presentation.viewmodel.MainViewModelFactory

@Module
interface MainViewModelFactoryModule {

    @Binds
    fun bindMainViewModelFactory(viewModelFactory: MainViewModelFactory): ViewModelProvider.Factory

}