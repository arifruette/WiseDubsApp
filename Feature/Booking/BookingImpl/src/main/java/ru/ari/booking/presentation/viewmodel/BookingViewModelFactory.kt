package ru.ari.booking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class BookingViewModelFactory @Inject constructor(
    private val bookingProvider: Provider<BookingViewModel>,
    private val bookingFormProvider: Provider<BookingFormViewModel>,
    private val myBookingsProvider: Provider<MyBookingsViewModel>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            BookingViewModel::class.java -> bookingProvider.get()
            BookingFormViewModel::class.java -> bookingFormProvider.get()
            MyBookingsViewModel::class.java -> myBookingsProvider.get()
            else -> error("Unknown ViewModel class: $modelClass")
        }
        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}
