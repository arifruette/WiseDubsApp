package ru.ari.wisedubsapp.di.modules

import dagger.Module
import ru.ari.login.di.modules.LoginRouteProviderModule
import ru.ari.registration.di.modules.RegistrationRouteProviderModule
import ru.ari.sharing.di.SharingScreenProviderModule

@Module(
    includes = [
        LoginRouteProviderModule::class,
        SharingScreenProviderModule::class,
        RegistrationRouteProviderModule::class
    ]
)
class NavigationEntriesModule