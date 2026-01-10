package ru.ari.cache.datastore

import kotlinx.coroutines.flow.Flow

interface DataStoreHelper {
    suspend fun saveToken(token: String)
    suspend fun getToken(): Flow<String?>
    suspend fun eraseToken()
}