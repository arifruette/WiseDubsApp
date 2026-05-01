package ru.ari.sharingpostdetails.api.di

import ru.ari.navigation.Navigator

fun interface SharingPostDetailsLauncher {
    fun open(
        navigator: Navigator,
        postId: Long,
        autoReserve: Boolean
    )
}
