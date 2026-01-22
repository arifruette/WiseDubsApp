package ru.ari.wisedubsapp.di.modules

import dagger.Module
import ru.ari.login.di.modules.LoginRouteProviderModule
import ru.ari.sharing.di.SharingScreenProviderModule

@Module(
    includes = [
        LoginRouteProviderModule::class,
        SharingScreenProviderModule::class
    ]
)
class NavigationEntriesModule