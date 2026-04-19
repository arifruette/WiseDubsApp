package ru.ari.managepost.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.ari.managepost.presentation.address.viewmodel.AddressManageViewModel
import javax.inject.Inject
import javax.inject.Provider

class ManagePostViewModelFactory @Inject constructor(
    private val managePostViewModelProvider: Provider<ManagePostViewModel>,
    private val addressManageViewModelProvider: Provider<AddressManageViewModel>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider = when (modelClass) {
            ManagePostViewModel::class.java -> managePostViewModelProvider
            AddressManageViewModel::class.java -> addressManageViewModelProvider
            else -> error("Unknown ViewModel class: $modelClass")
        }
        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }
}
