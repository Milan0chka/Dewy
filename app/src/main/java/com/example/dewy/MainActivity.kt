package com.example.dewy

import android.os.Bundle
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
import com.example.dewy.viewmodels.MainViewModel
import androidx.activity.viewModels


sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object MainPage : Screen("main")
    object Calendar : Screen("calendar")
    object Routines : Screen("routines")
    object Journal : Screen("journal")
    object Settings : Screen("settings")

}


class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DewyTheme {
                NavigationSetUp(mainViewModel)
            }
        }
    }
}

@Composable
fun NavigationSetUp(mainViewModel: MainViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Splash.route, builder = {
        composable(Screen.Splash.route){
            SplashScreen(navController)
        }
        composable(Screen.MainPage.route){
            MainPage(navController, mainViewModel = mainViewModel)
        }
        composable(Screen.Routines.route) {
            //TODO
        }
        composable(Screen.Calendar.route) {
            //TODO
        }
        composable(Screen.Journal.route) {
            //TODO
        }
        composable(Screen.Settings.route){
            //TODO
        }
    })
}