package com.example.dewy

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dewy.ui.pages.MainPage
import com.example.dewy.ui.pages.SplashScreen
import com.example.dewy.ui.theme.DewyTheme
import com.example.dewy.viewmodels.StreakViewModel
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dewy.ui.pages.JournalPage
import com.example.dewy.viewmodels.JournalViewModel
import com.example.dewy.viewmodels.TipViewModel


sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object MainPage : Screen("main")
    object Calendar : Screen("calendar")
    object Routines : Screen("routines")
    object Journal : Screen("journal")
    object Settings : Screen("settings")

}


class MainActivity : ComponentActivity() {

    private val streakViewModel: StreakViewModel by viewModels()
    private val tipViewModel: TipViewModel by viewModels()
    private val journalViewModel: JournalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DewyTheme {
                NavigationSetUp(
                    streakViewModel,
                    tipViewModel,
                    journalViewModel
                )
            }
        }

    }
}

@Composable
fun NavigationSetUp(
    streakViewModel: StreakViewModel,
    tipViewModel: TipViewModel,
    journalViewModel: JournalViewModel
){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Splash.route, builder = {
        composable(Screen.Splash.route){
            SplashScreen(navController)
        }
        composable(Screen.MainPage.route){
            MainPage(
                navController,
                streakViewModel,
                tipViewModel
            )
        }
        composable(Screen.Routines.route) {
            //TODO
        }
        composable(Screen.Calendar.route) {
            //TODO
        }
        composable(Screen.Journal.route) {
            JournalPage(
                navController,
                journalViewModel
            )
        }
        composable(Screen.Settings.route){
            //TODO
        }
    })
}