package ru.ari.myposts.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.ari.myposts.navigation.MyPostsScreenRouteProvider
import ru.ari.navigation.di.PostLoginRoutes
import ru.ari.navigation.di.RouteEntryProvider

@Module
interface MyPostsScreenProviderModule {

    @Binds
    @IntoSet
    @PostLoginRoutes
    fun bindMyPostsScreenProvider(
        myPostsScreenRouteProvider: MyPostsScreenRouteProvider
    ): RouteEntryProvider
}
