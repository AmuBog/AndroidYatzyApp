package com.example.yatzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yatzee.data.YatzyDatabase
import com.example.yatzee.ui.screens.menu.MenuScreen
import com.example.yatzee.ui.screens.game.YatzeeSheetScreen
import com.example.yatzee.ui.theme.YatzeeTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch {
            val db = YatzyDatabase.getInstance(this@MainActivity)
            db.scoreDao().deleteAllScores()
            db.scoreDao().deletePrimaryKeyIndex()
        }

        setContent {
            YatzeeTheme {
                YatzyNavGraph()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuScreenPreview() {
    YatzeeTheme {
        YatzyNavGraph()
    }
}

object Destinations {
    const val MAIN_MENU = "main_menu"
    const val YATZY_GAME = "yatzy_game"
}

@Composable
fun YatzyNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destinations.MAIN_MENU) {
        composable(route = Destinations.MAIN_MENU) {
            MenuScreen(
                navigateToYatzyGame = { navController.navigate(Destinations.YATZY_GAME) }
            )
        }
        composable(route = Destinations.YATZY_GAME) {
            YatzeeSheetScreen(
                backNavigation = { navController.navigateUp() }
            )
        }
    }

}
