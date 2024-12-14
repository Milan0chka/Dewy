package com.example.dewy.ui.pages


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.dewy.R
import com.example.dewy.Screen

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: Painter
)


@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val currentRoute = navController.currentBackStackEntryFlow.collectAsState(null).value?.destination?.route
    val isSplashScreen = currentRoute == Screen.Splash.route

    Scaffold(
        topBar = { if (!isSplashScreen) CustomTopBar(navController) },
        bottomBar = { if (!isSplashScreen) CustomBottomBar(navController) },
        floatingActionButton = {}
    ) { paddingValues ->
        val padding = if (!isSplashScreen) paddingValues else PaddingValues(0.dp)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            content()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    navHostController: NavHostController
){
    TopAppBar(
        modifier = Modifier.wrapContentHeight().clip(RoundedCornerShape(10.dp)),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        title = {
            Text(
                text = "Dewy",
                maxLines = 1,
                modifier = Modifier.padding(start = 5.dp)
            )
        },
        actions = {
            IconButton(onClick = {  }) {
                Icon(
                    painter = painterResource(R.drawable.icon_settings),
                    contentDescription = "Settings",
                    modifier = Modifier.padding(end = 10.dp).size(30.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    )
}

@Composable
fun CustomBottomBar(
    navHostController: NavHostController
) {
    val items = listOf(
        BottomNavItem(
            title = "Home",
            route = Screen.MainPage.route,
            icon = painterResource(R.drawable.icon_home)
        ),
        BottomNavItem(
            title = "Calendar",
            route = Screen.Calendar.route,
            icon = painterResource(R.drawable.icon_calendar)
        ),
        BottomNavItem(
            title = "Routine",
            route = Screen.Routines.route,
            icon = painterResource(R.drawable.icon_rountine)
        ),
        BottomNavItem(
            title = "Journal",
            route = Screen.Journal.route,
            icon = painterResource(R.drawable.icon_journal)
        )
    )

    // Get the current route from NavHostController
    val currentRoute = navHostController.currentBackStackEntryFlow.collectAsState(null).value?.destination?.route

    NavigationBar(
        modifier = Modifier.wrapContentHeight().clip(RoundedCornerShape(10.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        items.forEach { bottomNavItem ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = bottomNavItem.icon,
                        contentDescription = bottomNavItem.title,
                        modifier = Modifier.size(25.dp)
                    )
                },
                label = { Text(bottomNavItem.title, style = MaterialTheme.typography.labelMedium) },
                selected = currentRoute == bottomNavItem.route,
                onClick = {
                    if (currentRoute != bottomNavItem.route) {
                        navHostController.navigate(bottomNavItem.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    }
}
