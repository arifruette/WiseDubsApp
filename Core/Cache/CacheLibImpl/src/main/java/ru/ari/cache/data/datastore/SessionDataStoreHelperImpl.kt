package ru.ari.cache.data.datastore

import android.content.Context
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.ari.cache.domain.datastore.DataStoreHelper
import ru.ari.cache.domain.models.SessionState
import androidx.core.content.edit

class SessionDataStoreHelperImpl @Inject constructor(
    context: Context
) : DataStoreHelper {

    private val appContext = context.applicationContext
    private val sharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val sessionState = MutableStateFlow(readSessionState())

    override suspend fun saveSessionState(sessionState: SessionState) {
        sharedPreferences.edit {
            putString(KEY_TOKEN, sessionState.token)
                .putString(KEY_USER_LOGIN, sessionState.userLogin)
                .putLong(KEY_USER_ID, sessionState.userId ?: NO_USER_ID)
        }
        this.sessionState.value = sessionState
    }

    override suspend fun getSessionState(): Flow<SessionState?> = sessionState.asStateFlow()

    override suspend fun eraseSessionState() {
        sharedPreferences.edit { clear() }
        sessionState.value = null
    }

    private fun readSessionState(): SessionState? {
        val token = sharedPreferences.getString(KEY_TOKEN, null) ?: return null
        val userLogin = sharedPreferences.getString(KEY_USER_LOGIN, null) ?: return null
        val rawUserId = sharedPreferences.getLong(KEY_USER_ID, NO_USER_ID)

        return SessionState(
            token = token,
            userLogin = userLogin,
            userId = rawUserId.takeUnless { it == NO_USER_ID }
        )
    }

    private companion object {
        private const val PREFS_NAME = "wise_dubs_session"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_LOGIN = "user_login"
        private const val KEY_USER_ID = "user_id"
        private const val NO_USER_ID = Long.MIN_VALUE
    }
}
