package ru.ari.navigation

import androidx.navigation3.runtime.NavKey
import kotlin.collections.last
import kotlin.collections.removeLastOrNull

class BaseNavigatorImpl(private val state: NavigationState): Navigator {
    override fun navigate(route: NavKey){
        if (route in state.backStacks.keys){
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    override fun goBack(){
        val currentStack = state.backStacks[state.topLevelRoute] ?:
        error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        if (currentRoute == state.topLevelRoute){
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}