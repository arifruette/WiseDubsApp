package ru.ari.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import javax.inject.Provider
import javax.inject.Singleton

@Module
class CoreModule {
    
    @Provides
    @Singleton
    fun provideViewModelFactory(
        creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
    ): ViewModelProvider.Factory {
        return ViewModelFactory(creators)
    }

}
