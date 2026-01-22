package ru.ari.cache.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.ari.cache.domain.datastore.DataStoreHelper
import ru.ari.cache.domain.models.SessionState
import javax.inject.Inject

class MockDataStoreHelperImpl @Inject constructor(): DataStoreHelper {
    override suspend fun saveSessionState(sessionState: SessionState) = Unit

    override suspend fun getSessionState(): Flow<SessionState?> = flowOf(null)

    override suspend fun eraseSessionState() = Unit
}