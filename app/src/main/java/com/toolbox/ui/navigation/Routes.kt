package com.toolbox.ui.navigation

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Category : Screen("category/{category}") {
        fun createRoute(category: String) = "category/$category"
    }
    data object Tool : Screen("tool/{toolId}") {
        fun createRoute(toolId: String) = "tool/$toolId"
    }
    data object History : Screen("history")
}
