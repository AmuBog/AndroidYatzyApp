package com.example.yatzy.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yatzy.GameState
import com.example.yatzy.models.GameType
import com.example.yatzy.ui.screens.game.ForcedYatzyScreen
import com.example.yatzy.ui.screens.game.YatzySheetScreen
import com.example.yatzy.ui.screens.highscore.HighscoreScreen
import com.example.yatzy.ui.screens.menu.MenuScreen

object Destinations {
    const val MAIN_MENU = "main_menu"
    const val YATZY_GAME = "yatzy_game"
    const val HIGHSCORE = "highscore"
}

@Composable
fun YatzyApp(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Destinations.MAIN_MENU) {
        composable(route = Destinations.MAIN_MENU) {
            MenuScreen(
                navigateToYatzyGame = { navController.navigate(Destinations.YATZY_GAME) },
                navigateToHighscores = { navController.navigate(Destinations.HIGHSCORE) }
            )
        }
        composable(route = Destinations.YATZY_GAME) {
            when (GameState.gameType) {
                GameType.FORCED -> {
                    ForcedYatzyScreen(
                        backNavigation = { navController.navigateUp() }
                    )
                }

                GameType.FREE -> {
                    YatzySheetScreen(
                        backNavigation = { navController.navigateUp() }
                    )
                }
            }
        }
        composable(route = Destinations.HIGHSCORE) {
            HighscoreScreen(
                backNavigation = { navController.navigateUp() }
            )
        }
    }
}
