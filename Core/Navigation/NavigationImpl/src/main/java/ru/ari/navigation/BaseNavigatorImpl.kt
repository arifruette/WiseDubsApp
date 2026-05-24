package ru.ari.navigation

import androidx.navigation3.runtime.NavKey

class BaseNavigatorImpl(private val state: NavigationState): Navigator {
    override fun navigate(route: NavKey){
        if (route in state.backStacks.keys){
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    override fun replaceStack(topLevelRoute: NavKey, vararg routes: NavKey) {
        val currentTopLevelRoute = state.topLevelRoute
        if (currentTopLevelRoute != topLevelRoute) {
            resetStack(currentTopLevelRoute)
        }
        resetStack(topLevelRoute, routes.toList())
        state.topLevelRoute = topLevelRoute
    }

    private fun resetStack(topLevelRoute: NavKey, routes: List<NavKey> = emptyList()) {
        val stack = state.backStacks[topLevelRoute] ?: error("Stack for $topLevelRoute not found")
        stack.clear()
        stack.add(topLevelRoute)
        routes.forEach { stack.add(it) }
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
