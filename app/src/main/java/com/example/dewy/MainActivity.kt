package com.example.dewy

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dewy.ui.pages.MainPage
import com.example.dewy.ui.pages.SplashScreen
import com.example.dewy.ui.theme.DewyTheme
import com.example.dewy.viewmodels.StreakViewModel
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.dewy.ui.pages.CalendarPage
import com.example.dewy.ui.pages.JournalPage
import com.example.dewy.ui.pages.MainScaffold
import com.example.dewy.ui.pages.RoutineBuilderScreen
import com.example.dewy.ui.pages.RoutineGuideScreen
import com.example.dewy.ui.pages.RoutinePage
import com.example.dewy.viewmodels.JournalViewModel
import com.example.dewy.viewmodels.RoutineBuilderViewModel
import com.example.dewy.viewmodels.RoutineViewModel
import com.example.dewy.viewmodels.TipViewModel
import com.example.dewy.viewmodels.UVIndexViewModel
import android.Manifest
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.platform.LocalView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow

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
    object RoutineGuide : Screen("routine_guide/{type}"){
        fun createRoute(type: String): String{
            return "routine_guide/$type"
        }
    }

}

class MainActivity : ComponentActivity() {

    private val streakViewModel: StreakViewModel by viewModels()
    private val tipViewModel: TipViewModel by viewModels()
    private val journalViewModel: JournalViewModel by viewModels()
    private val routineViewModel: RoutineViewModel by viewModels()
    private val routineBuilderViewModel: RoutineBuilderViewModel by viewModels()
    private val uvIndexViewModel: UVIndexViewModel by viewModels()

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationFlow = MutableStateFlow<Location?>(null)

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun handlePermissionResults(permissions: Map<String, Boolean>) {
        val deniedPermissions = permissions.filterValues { !it }
        if (deniedPermissions.isEmpty()) {
            println("All permissions granted")
        } else {
            println("The following permissions were denied: ${deniedPermissions.keys}")
        }
    }

    private fun getLocation(onLocationReceived: (Location) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        onLocationReceived(location)
                        println("Location: Lat = ${location.latitude}, Lon = ${location.longitude}")
                    } else {
                        println("Location is null. Cannot get last location.")
                    }
                }
                .addOnFailureListener { e ->
                    println("Failed to get location: ${e.message}")
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            handlePermissionResults(permissions)
        }

        checkAndRequestPermissions()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation { location ->
            uvIndexViewModel.setLocation(location)
        }

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
                        routineBuilderViewModel,
                        uvIndexViewModel
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
    routineBuilderViewModel: RoutineBuilderViewModel,
    uvIndexViewModel: UVIndexViewModel
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

        composable(
            Screen.RoutineGuide.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val type = navBackStackEntry.arguments?.getString("type")
            if (type != null) {
                RoutineGuideScreen(
                    navController,
                    type,
                    routineViewModel,
                    uvIndexViewModel
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