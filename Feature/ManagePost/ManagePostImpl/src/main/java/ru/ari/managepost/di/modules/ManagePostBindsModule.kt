package ru.ari.managepost.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.ari.managepost.data.repository.PickupLocationRepositoryImpl
import ru.ari.managepost.domain.repository.PickupLocationRepository
import ru.ari.managepost.presentation.viewmodel.ManagePostViewModelFactory

@Module
interface ManagePostBindsModule {

    @Binds
    fun bindManagePostViewModelFactory(
        factory: ManagePostViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    fun bindPickupLocationRepository(
        impl: PickupLocationRepositoryImpl
    ): PickupLocationRepository
}
