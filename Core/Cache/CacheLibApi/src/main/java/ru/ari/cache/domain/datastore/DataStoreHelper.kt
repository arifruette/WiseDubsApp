package ru.ari.cache.domain.datastore

import kotlinx.coroutines.flow.Flow
import ru.ari.cache.domain.models.SessionState

interface DataStoreHelper {
    suspend fun saveSessionState(sessionState: SessionState)
    suspend fun getSessionState(): Flow<SessionState?>
    suspend fun eraseSessionState()
}