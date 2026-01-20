package ru.ari.di.viewmodel

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.multibindings.Multibinds

@Module
interface CoreBindsModule {

    @Multibinds
    fun provideEmptyViewModelCreator(): Map<Class<out ViewModel>, @JvmSuppressWildcards ViewModel>

}