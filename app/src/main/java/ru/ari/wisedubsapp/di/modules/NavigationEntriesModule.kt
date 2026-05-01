package ru.ari.wisedubsapp.di.modules

import dagger.Module
import ru.ari.login.di.modules.LoginRouteProviderModule
import ru.ari.managepost.di.ManagePostScreenProviderModule
import ru.ari.myposts.di.MyPostsScreenProviderModule
import ru.ari.registration.di.modules.RegistrationRouteProviderModule
import ru.ari.sharing.di.SharingScreenProviderModule
import ru.ari.sharingpostdetails.di.SharingPostDetailsScreenProviderModule

@Module(
    includes = [
        LoginRouteProviderModule::class,
        ManagePostScreenProviderModule::class,
        MyPostsScreenProviderModule::class,
        SharingScreenProviderModule::class,
        SharingPostDetailsScreenProviderModule::class,
        RegistrationRouteProviderModule::class
    ]
)
class NavigationEntriesModule
