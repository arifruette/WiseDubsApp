package ru.ari.wisedubsapp.di

import ru.ari.di.DepsProvider


fun interface DepsFactory {
    operator fun invoke(): Any
}

class DepsStore : DepsProvider {

    private val factories = mutableMapOf<Class<*>, DepsFactory>()

    fun <T : Any> register(key: Class<T>, factory: DepsFactory) {
        factories[key] = factory
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getDeps(key: Class<T>): T {
        val factory = factories[key]
            ?: error("Deps for ${key.name} not registered")
        return factory.invoke() as T
    }
}