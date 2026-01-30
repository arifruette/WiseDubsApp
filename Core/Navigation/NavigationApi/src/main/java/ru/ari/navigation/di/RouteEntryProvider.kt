package ru.ari.navigation.di

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

@Stable
interface RouteEntryProvider {

    fun EntryProviderScope<NavKey>.provideRoute()

}