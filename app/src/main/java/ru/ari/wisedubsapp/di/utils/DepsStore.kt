package ru.ari.wisedubsapp.di.utils

import ru.ari.di.DepsProvider
import java.util.concurrent.ConcurrentHashMap


fun interface DepsFactory {
    operator fun invoke(): Any
}

class DepsStore : DepsProvider {

    private val factories = mutableMapOf<Class<*>, DepsFactory>()
    private val instances = mutableMapOf<Class<*>, Any>()

    fun <T : Any> register(key: Class<T>, factory: DepsFactory) {
        factories[key] = factory
        instances.remove(key)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getDeps(key: Class<T>): T {
        val cached = instances[key]
        if (cached != null) return cached as T

        val factory = factories[key]
            ?: error("Deps for ${key.name} not registered")

        val created = factory.invoke()

        instances[key] = created
        return created as T
    }
}
