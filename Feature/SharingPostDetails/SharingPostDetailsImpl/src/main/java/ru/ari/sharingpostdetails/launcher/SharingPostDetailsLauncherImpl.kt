package ru.ari.sharingpostdetails.launcher

import javax.inject.Inject
import ru.ari.navigation.Navigator
import ru.ari.navigation.Route
import ru.ari.sharingpostdetails.api.di.SharingPostDetailsLauncher

class SharingPostDetailsLauncherImpl @Inject constructor() : SharingPostDetailsLauncher {
    override fun open(
        navigator: Navigator,
        postId: Long,
        autoReserve: Boolean
    ) {
        navigator.navigate(
            Route.PostLogin.SharingPostDetailsRoute(
                postId = postId,
                autoReserve = autoReserve
            )
        )
    }
}
