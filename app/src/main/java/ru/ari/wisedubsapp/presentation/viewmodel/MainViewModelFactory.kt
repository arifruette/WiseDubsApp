package ru.ari.wisedubsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class MainViewModelFactory @Inject constructor(
    private val provider: Provider<MainViewModel>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != MainViewModel::class.java) {
            error("Unknown ViewModel class: $modelClass")
        }
        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }

}