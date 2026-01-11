package ru.ari.navigation

import dagger.Module
import ru.ari.login.di.LoginRouteProviderModule
import ru.ari.sharing.di.SharingScreenProviderModule

@Module(
    includes = [
        LoginRouteProviderModule::class,
        SharingScreenProviderModule::class
    ]
)
class NavigationEntriesModule