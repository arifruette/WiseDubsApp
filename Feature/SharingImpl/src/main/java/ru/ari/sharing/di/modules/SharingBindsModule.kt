package ru.ari.sharing.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.ari.sharing.api.domain.interactor.SharingInteractor
import ru.ari.sharing.api.domain.repository.SharingRepository
import ru.ari.sharing.data.repository.SharingRepositoryImpl
import ru.ari.sharing.domain.interactor.SharingInteractorImpl
import ru.ari.sharing.domain.mapper.SharingPostUiMapper
import ru.ari.sharing.presentation.mappers.SharingPostUiMapperImpl
import ru.ari.sharing.presentation.viewmodel.SharingViewModelFactory

@Module
interface SharingBindsModule {

    @Binds
    fun bindSharingViewModelFactory(
        sharingViewModelFactory: SharingViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    fun bindSharingRepository(
        sharingRepositoryImpl: SharingRepositoryImpl
    ): SharingRepository

    @Binds
    fun bindSharingInteractor(
        sharingInteractorImpl: SharingInteractorImpl
    ): SharingInteractor

    @Binds
    fun bindSharingPostUiMapper(
        sharingPostUiMapperImpl: SharingPostUiMapperImpl
    ): SharingPostUiMapper
}
