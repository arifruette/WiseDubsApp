package ru.ari.composelib.di.utils

import androidx.lifecycle.ViewModel

internal class ScopedComponentHolder<T>(
    val component: T
) : ViewModel()