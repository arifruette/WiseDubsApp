package ru.ari.sharingpostdetails.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class SharingPostDetailsViewModelFactory @Inject constructor(
    private val provider: Provider<SharingPostDetailsViewModel>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != SharingPostDetailsViewModel::class.java) {
            error("Unknown ViewModel class: $modelClass")
        }
        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }
}
