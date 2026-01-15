package ru.ari.composelib

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider

val LocalViewModelProvider = staticCompositionLocalOf<ViewModelProvider.Factory> {
    error("LocalViewModelProvider is missing")
}