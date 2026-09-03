package com.toolbox.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.toolbox.domain.model.ToolCategory
import com.toolbox.ui.screens.CategoryScreen
import com.toolbox.ui.screens.HistoryScreen
import com.toolbox.ui.screens.MainScreen
import com.toolbox.ui.screens.ToolScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Main.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Main.route) {
            MainScreen(
                onCategoryClick = { category ->
                    navController.navigate(Screen.Category.createRoute(category.name))
                },
                onHistoryClick = {
                    navController.navigate(Screen.History.route)
                }
            )
        }
        composable(
            route = Screen.Category.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("category") ?: return@composable
            val category = ToolCategory.valueOf(categoryName)
            CategoryScreen(
                category = category,
                onToolClick = { toolId ->
                    navController.navigate(Screen.Tool.createRoute(toolId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Tool.route,
            arguments = listOf(navArgument("toolId") { type = NavType.StringType })
        ) { backStackEntry ->
            val toolId = backStackEntry.arguments?.getString("toolId") ?: return@composable
            ToolScreen(
                toolId = toolId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
