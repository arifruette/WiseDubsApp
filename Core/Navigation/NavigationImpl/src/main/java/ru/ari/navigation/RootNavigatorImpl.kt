package ru.ari.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Отличие от базовой реализации в том, что у глобальных рутов (PreLogin/PostLogin)
 * нет бэкстэка (кроме самого глобального рута в нем), поэтому логика переходов иная
 */
class RootNavigatorImpl(private val state: NavigationState): Navigator {
    override fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            // при перемещении по "глобальным рутам" остальные бэкстэки должны быть очищены
            // чтобы не было возможности в них вернуться
            state.backStacks.keys.forEach {
                if (it != route) {
                    state.backStacks[it]?.clear()
                }
            }
            state.topLevelRoute = route
        }
    }

    override fun replaceStack(topLevelRoute: NavKey, vararg routes: NavKey) {
        require(routes.isEmpty()) { "Root navigator does not support nested stack routes" }
        if (topLevelRoute in state.backStacks.keys) {
            state.backStacks.forEach { (route, stack) ->
                stack.clear()
                stack.add(route)
            }
            state.topLevelRoute = topLevelRoute
        }
    }

    override fun goBack() {
        state.backStacks[state.topLevelRoute]?.removeLastOrNull()
    }
}
