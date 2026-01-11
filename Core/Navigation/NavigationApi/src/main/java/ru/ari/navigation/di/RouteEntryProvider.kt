package ru.ari.navigation.di

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

interface RouteEntryProvider {

    fun EntryProviderScope<NavKey>.provideRoute()

}