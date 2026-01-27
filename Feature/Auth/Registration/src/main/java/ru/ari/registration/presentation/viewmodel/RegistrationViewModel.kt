package ru.ari.registration.presentation.viewmodel

import androidx.lifecycle.ViewModel
import ru.ari.auth.common.api.domain.interactor.AuthInteractor
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(
    private val authInteractor: AuthInteractor
): ViewModel()