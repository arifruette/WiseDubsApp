package ru.ari.di

import android.content.Context

interface DepsProvider {
    fun <T : Any> getDeps(key: Class<T>): T
}

inline fun <reified T : Any> Context.deps(): T {
    val provider = applicationContext as DepsProvider
    return provider.getDeps(T::class.java)
}