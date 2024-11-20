package com.example.dewy.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.dewy.R
import com.example.dewy.Screen

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: Painter
)


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScaffold(
    navHostController: NavHostController,
    showFloatingButton: Boolean = false,
    content: @Composable () -> Unit
){
    Scaffold(
        topBar = { CustomTopBar(navHostController) },
        bottomBar = { CustomBottomBar(navHostController) },
        floatingActionButton = {}
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
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
        modifier = Modifier.clip(RoundedCornerShape(10.dp)),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        title = {
            Text(
                text = "Dewy",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 10.dp)
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
){
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

    var selectedItem by remember { mutableIntStateOf(0) }

    NavigationBar(
        modifier = Modifier.clip(RoundedCornerShape(10.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        tonalElevation = NavigationBarDefaults.Elevation
    ) { items.forEachIndexed { index, bottomNavItem ->
        NavigationBarItem(
            icon = {
                Icon(
                    painter = bottomNavItem.icon,
                    contentDescription = bottomNavItem.title
                )
            },
            label = { Text(bottomNavItem.title, style = MaterialTheme.typography.labelLarge) },
            selected = selectedItem == index,
            onClick = {
                selectedItem = index
                //TODO navHostController.navigate({})
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