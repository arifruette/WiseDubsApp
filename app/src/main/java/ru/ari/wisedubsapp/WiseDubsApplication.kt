package ru.ari.wisedubsapp

import android.app.Application
import ru.ari.cache.datastore.di.DaggerDataStoreHelperComponent
import ru.ari.cache.di.DataStoreDeps
import ru.ari.di.DepsProvider
import ru.ari.wisedubsapp.di.AppComponent
import ru.ari.wisedubsapp.di.DaggerAppComponent
import ru.ari.wisedubsapp.di.DepsStore

class WiseDubsApplication : Application(), DepsProvider {

    lateinit var appComponent: AppComponent
        private set

    val depsStore = DepsStore().apply {
        register(DataStoreDeps::class.java) {
            DaggerDataStoreHelperComponent.create()
        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(getDeps(DataStoreDeps::class.java))
    }

    override fun <T : Any> getDeps(key: Class<T>): T = depsStore.getDeps(key)

}