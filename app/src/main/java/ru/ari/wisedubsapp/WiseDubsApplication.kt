package ru.ari.wisedubsapp

import android.app.Application
import ru.ari.auth.common.api.di.AuthCommonApi
import ru.ari.auth.common.impl.di.DaggerAuthCommonComponent
import ru.ari.cache.di.CacheApi
import ru.ari.cache.di.DaggerCacheLibComponent
import ru.ari.di.DepsProvider
import ru.ari.di.deps
import ru.ari.network.di.DaggerNetworkComponent
import ru.ari.network.di.NetworkApi
import ru.ari.posts.api.di.PostsApi
import ru.ari.posts.di.DaggerPostsComponent
import ru.ari.wisedubsapp.di.component.AppComponent
import ru.ari.wisedubsapp.di.component.DaggerAppComponent
import ru.ari.wisedubsapp.di.utils.DepsStore

class WiseDubsApplication : Application(), DepsProvider {

    lateinit var appComponent: AppComponent
        private set

    val depsStore = DepsStore().apply {
        register(CacheApi::class.java) {
            DaggerCacheLibComponent.factory().create(applicationContext)
        }
        register(NetworkApi::class.java) {
            DaggerNetworkComponent.factory().create(
                baseUrl = BuildConfig.BASE_API_URL,
                cacheApi = deps()
            )
        }
        register(AuthCommonApi::class.java) {
            DaggerAuthCommonComponent.factory().create(deps(), deps())
        }
        register(PostsApi::class.java) {
            DaggerPostsComponent.factory().create(
                networkApi = deps(),
                cacheApi = deps(),
                context = applicationContext
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(getDeps(CacheApi::class.java))
    }

    override fun <T : Any> getDeps(key: Class<T>): T = depsStore.getDeps(key)

}
