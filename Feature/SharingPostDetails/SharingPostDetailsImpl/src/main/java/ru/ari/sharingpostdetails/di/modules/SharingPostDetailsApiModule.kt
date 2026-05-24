package ru.ari.sharingpostdetails.di.modules

import dagger.Binds
import dagger.Module
import ru.ari.sharingpostdetails.api.di.SharingPostDetailsLauncher
import ru.ari.sharingpostdetails.launcher.SharingPostDetailsLauncherImpl

@Module
interface SharingPostDetailsApiModule {

    @Binds
    fun bindSharingPostDetailsLauncher(
        launcher: SharingPostDetailsLauncherImpl
    ): SharingPostDetailsLauncher
}
