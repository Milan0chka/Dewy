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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.dewy.ui.pages.CalendarPage
import com.example.dewy.ui.pages.JournalPage
import com.example.dewy.ui.pages.MainScaffold
import com.example.dewy.ui.pages.RoutineBuilderScreen
import com.example.dewy.ui.pages.RoutinePage
import com.example.dewy.viewmodels.JournalViewModel
import com.example.dewy.viewmodels.RoutineBuilderViewModel
import com.example.dewy.viewmodels.RoutineViewModel
import com.example.dewy.viewmodels.TipViewModel


sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object MainPage : Screen("main")
    object Calendar : Screen("calendar")
    object Routines : Screen("routines")
    object Journal : Screen("journal")
    object Settings : Screen("settings")
    object RoutineBuilder : Screen("routine_builder/{type}"){
        fun createRoute(type:String): String{
            return "routine_builder/$type"
        }
    }

}


class MainActivity : ComponentActivity() {

    private val streakViewModel: StreakViewModel by viewModels()
    private val tipViewModel: TipViewModel by viewModels()
    private val journalViewModel: JournalViewModel by viewModels()
    private val routineViewModel: RoutineViewModel by viewModels()
    private val routineBuilderViewModel: RoutineBuilderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DewyTheme {
                val navController = rememberNavController()

                MainScaffold(navController) {
                    NavigationSetUp(
                        navController,
                        streakViewModel,
                        tipViewModel,
                        journalViewModel,
                        routineViewModel,
                        routineBuilderViewModel
                    )
                }
            }
        }
    }
}


@Composable
fun NavigationSetUp(
    navController: NavHostController,
    streakViewModel: StreakViewModel,
    tipViewModel: TipViewModel,
    journalViewModel: JournalViewModel,
    routineViewModel: RoutineViewModel,
    routineBuilderViewModel: RoutineBuilderViewModel
){
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
            RoutinePage(
                navController,
                routineViewModel
            )
        }

        composable(
            Screen.RoutineBuilder.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val type = navBackStackEntry.arguments?.getString("type")
            if (type != null) {
                RoutineBuilderScreen(
                    navController,
                    type,
                    routineViewModel,
                    routineBuilderViewModel
                )
            }
        }

        composable(Screen.Calendar.route) {
            CalendarPage(
                navController
            )
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