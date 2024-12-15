package com.example.dewy.ui.pages

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.dewy.viewmodels.RoutineViewModel
import com.example.dewy.viewmodels.UVIndexViewModel

@Composable
fun RoutineGuideScreen(
    navController: NavController,
    type: String,
    routineViewModel: RoutineViewModel,
    uvIndexViewModel: UVIndexViewModel
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
            UVIndexScreen(uvIndexViewModel)
        }
    }
}


@Composable
fun UVIndexScreen(
    viewModel: UVIndexViewModel
) {
    val uvIndex by viewModel.uvIndex.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUVIndex() // Call it when the screen is loaded
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "UV Index",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uvIndex != null) {
            Text("UV Index: ${uvIndex ?: "N/A"}")

        } else {
            LoadingSpinner()
        }
    }
}