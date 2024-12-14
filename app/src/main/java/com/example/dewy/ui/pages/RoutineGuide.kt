package com.example.dewy.ui.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.dewy.viewmodels.RoutineViewModel

@Composable
fun RoutineGuideScreen(
    navController: NavController,
    type: String,
    routineViewModel: RoutineViewModel
){
    val index = if (type == "Morning") 0 else 1
    val todayRoutine = routineViewModel.todayRoutines.collectAsState().value?.getOrNull(index)

    if (todayRoutine == null){
        InfoCard {
            Text("Oops.. You have run into error.")
            Text("Please try again later.")
        }
    } else {
        InfoCard {
            Text("$todayRoutine")
        }
    }
}