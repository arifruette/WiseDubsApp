package ru.ari.booking.presentation.contract

sealed interface BookingFormUiEffect {
    data object NavigateBack : BookingFormUiEffect
    data class Completed(val isCreate: Boolean, val message: String) : BookingFormUiEffect
    data class ShowMessage(val message: String) : BookingFormUiEffect
}
