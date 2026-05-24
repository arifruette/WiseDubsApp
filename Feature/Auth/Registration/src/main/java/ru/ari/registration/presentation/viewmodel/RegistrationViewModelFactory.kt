package ru.ari.registration.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class RegistrationViewModelFactory @Inject constructor(
    private val provider: Provider<RegistrationViewModel>
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != RegistrationViewModel::class.java) {
            error("Unknown ViewModel class: $modelClass")
        }
        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }

}