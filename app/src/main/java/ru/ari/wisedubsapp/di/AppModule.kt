package ru.ari.wisedubsapp.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.ari.di.viewmodel.ViewModelKey
import ru.ari.wisedubsapp.MainViewModel
import javax.inject.Singleton

@Module
interface AppModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Singleton
    fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel
}