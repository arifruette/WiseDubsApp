package ru.ari.navigation.postlogin.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

@Immutable
internal data class NavBarItem(
    @param:DrawableRes val iconRes: Int,
    val label: String
)