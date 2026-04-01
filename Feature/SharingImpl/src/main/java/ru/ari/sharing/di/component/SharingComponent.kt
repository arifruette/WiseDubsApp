package ru.ari.sharing.di.component

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.cache.di.CacheApi
import ru.ari.network.di.NetworkApi
import ru.ari.sharing.di.modules.SharingBindsModule
import ru.ari.sharing.di.modules.SharingDataModule
import ru.ari.sharing.di.scope.SharingScreenScope

@SharingScreenScope
@Component(
    modules = [SharingBindsModule::class, SharingDataModule::class],
    dependencies = [NetworkApi::class, CacheApi::class]
)
interface SharingComponent {

    val sharingViewModelFactory: ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            networkApi: NetworkApi,
            cacheApi: CacheApi
        ): SharingComponent
    }
}
