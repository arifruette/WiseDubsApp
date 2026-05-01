package ru.ari.profile.presentation.contract

import androidx.compose.runtime.Immutable
import ru.ari.profile.presentation.models.ProfileUiModel

@Immutable
data class ProfileScreenUiState(
    val isProfileLoading: Boolean = true,
    val profile: ProfileUiModel? = null,
    val profileError: String? = null,
    val showLogoutDialog: Boolean = false
)
