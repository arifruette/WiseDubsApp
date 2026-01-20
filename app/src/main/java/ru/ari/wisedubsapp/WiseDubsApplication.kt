package ru.ari.wisedubsapp

import android.app.Application
import ru.ari.cache.datastore.di.DaggerCacheLibComponent
import ru.ari.cache.di.CacheDeps
import ru.ari.di.DepsProvider
import ru.ari.network.di.DaggerNetworkComponent
import ru.ari.network.di.NetworkDeps
import ru.ari.wisedubsapp.di.AppComponent
import ru.ari.wisedubsapp.di.DaggerAppComponent
import ru.ari.wisedubsapp.di.DepsStore

class WiseDubsApplication : Application(), DepsProvider {

    lateinit var appComponent: AppComponent
        private set

    val depsStore = DepsStore().apply {
        register(CacheDeps::class.java) {
            DaggerCacheLibComponent.create()
        }
        register(NetworkDeps::class.java) {
            DaggerNetworkComponent.factory().create(
                baseUrl = BuildConfig.BASE_API_URL,
                cacheDeps = getDeps(CacheDeps::class.java)
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(getDeps(CacheDeps::class.java))
    }

    override fun <T : Any> getDeps(key: Class<T>): T = depsStore.getDeps(key)

}