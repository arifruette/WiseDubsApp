package ru.ari.composelib.di.utils

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun <T : Any> rememberScopedComponent(
    key: String? = null,
    componentProvider: () -> T
): T {
    // получаем local view model store owner чтоб не париться
    val holder: ScopedComponentHolder<T> = viewModel(
        key = key,
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                return ScopedComponentHolder(componentProvider()) as VM
            }
        }
    )
    return holder.component
}