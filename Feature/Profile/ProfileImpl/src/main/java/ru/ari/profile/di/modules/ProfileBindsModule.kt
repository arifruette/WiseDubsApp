package ru.ari.profile.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.ari.profile.presentation.viewmodel.ProfileViewModelFactory

@Module
interface ProfileBindsModule {

    @Binds
    fun bindProfileViewModelFactory(
        profileViewModelFactory: ProfileViewModelFactory
    ): ViewModelProvider.Factory
}
