package ru.ari.network

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.ari.cache.domain.datastore.DataStoreHelper
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataStoreHelper: DataStoreHelper
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // TODO: предусмотреть logout при невалидном токене (лучше вынести в отдельное место)
        val token = runBlocking { dataStoreHelper.getSessionState().firstOrNull()?.token }

        val req = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else chain.request()

        return chain.proceed(req)
    }
}
