package ru.ari.navigation

import androidx.navigation3.runtime.NavKey

interface Navigator {

    fun navigate(route: NavKey)

    fun goBack()

}