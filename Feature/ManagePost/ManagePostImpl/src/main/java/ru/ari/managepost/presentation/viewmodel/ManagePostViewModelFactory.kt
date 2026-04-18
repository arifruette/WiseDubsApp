package ru.ari.managepost.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class ManagePostViewModelFactory @Inject constructor(
    private val provider: Provider<ManagePostViewModel>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != ManagePostViewModel::class.java) {
            error("Unknown ViewModel class: $modelClass")
        }
        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }
}
