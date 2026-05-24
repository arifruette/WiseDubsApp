package ru.ari.sharingpostdetails.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.ari.sharingpostdetails.presentation.mappers.SharingPostDetailsUiMapper
import ru.ari.sharingpostdetails.presentation.viewmodel.SharingPostDetailsViewModelFactory

@Module
interface SharingPostDetailsBindsModule {

    @Binds
    fun bindSharingPostDetailsViewModelFactory(
        factory: SharingPostDetailsViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    fun bindSharingPostDetailsUiMapper(
        mapper: SharingPostDetailsUiMapper.Impl
    ): SharingPostDetailsUiMapper
}
