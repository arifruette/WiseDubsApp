package ru.ari.wisedubsapp.di.modules

import dagger.Module
import ru.ari.booking.di.BookingScreenProviderModule
import ru.ari.login.di.modules.LoginRouteProviderModule
import ru.ari.managepost.di.ManagePostScreenProviderModule
import ru.ari.myposts.di.MyPostsScreenProviderModule
import ru.ari.profile.di.ProfileScreenProviderModule
import ru.ari.registration.di.modules.RegistrationRouteProviderModule
import ru.ari.sharing.di.SharingScreenProviderModule
import ru.ari.sharingpostdetails.di.SharingPostDetailsScreenProviderModule

@Module(
    includes = [
        LoginRouteProviderModule::class,
        BookingScreenProviderModule::class,
        ManagePostScreenProviderModule::class,
        MyPostsScreenProviderModule::class,
        SharingScreenProviderModule::class,
        SharingPostDetailsScreenProviderModule::class,
        ProfileScreenProviderModule::class,
        RegistrationRouteProviderModule::class
    ]
)
class NavigationEntriesModule
