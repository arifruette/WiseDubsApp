package ru.ari.cache.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class MockDataStoreHelperImpl @Inject constructor(): DataStoreHelper {
    override suspend fun saveToken(token: String) = Unit

    override suspend fun getToken(): Flow<String?> = flowOf(null)

    override suspend fun eraseToken() = Unit
}