package ru.ari.wisedubsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.ari.cache.datastore.DataStoreHelper
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val dataStoreHelper: DataStoreHelper
): ViewModel() {
    init {
        viewModelScope.launch {
            val token = dataStoreHelper.getToken().firstOrNull()
            println(token)
        }
    }
}