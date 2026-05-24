package ru.ari.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class ReservedPostsViewModelFactory @Inject constructor(
    private val provider: Provider<ReservedPostsViewModel>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != ReservedPostsViewModel::class.java) {
            error("Unknown ViewModel class: $modelClass")
        }
        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }
}
