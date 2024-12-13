package com.example.dewy.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dewy.Screen
import com.example.dewy.viewmodels.RoutineViewModel

//first open - no routine in database - ask to start forming it
//routine builder - start with morning routine then night. before each - small info page
//routine builder - each routine has 5 steps - clean, exfoliate(only evening), hydrate, treat, mosturise and spf(only in morning)
//routine builder - on each step user main product and optional add list of all products he have in this category (name, key ingredients, oftes of use)
//ask for prefered time  of routine
//give feedback is any products conflict so get rid of one

//on next launches - routine in database
//2 cards - day and morning with 2 buttons - start and edit
//on start just do thought routine telling what to use today (check UF index in morning, humidity sensor)
// on edit one routine builder

//receive notification on needed time

@Composable
fun RoutinePage(
    navController: NavController,
    routineViewModel: RoutineViewModel
){
    val routines by routineViewModel.routines.collectAsState()

    LaunchedEffect(Unit) {
        if (routines == null)
            routineViewModel.fetchRoutines()
    }
    if (routines == null) {
        LoadingSpinner()
//    } else if (routines!!.isEmpty()){
//        NoRoutineFound(navController)
//    }
    }   else {
        NoRoutineFound(navController)
    }

}

@Composable
fun NoRoutineFound(navController: NavController){
    InfoCard {
        Text(
            text ="No routine found!",
            style = MaterialTheme.typography.titleSmall)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Text(
                text = "It seems like you have not build your skincare routine yet.\n\n" +
                        "Click a button below to start building it.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
                Button(
                    onClick = {navController.navigate(Screen.RoutineBuilder.createRoute("Morning"))},
                    colors = ButtonDefaults.buttonColors(
                        Color.White,
                        Color.DarkGray
                    ),
                    border = BorderStroke(1.dp, Color.DarkGray),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Build morning routine",
                        fontWeight = FontWeight(600)
                    )
                }
                Button(
                    onClick = {navController.navigate(Screen.RoutineBuilder.createRoute("Evening"))},
                    colors = ButtonDefaults.buttonColors(
                        Color.DarkGray,
                        Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Build evening routine",
                        fontWeight = FontWeight(600)
                    )
                }

        }
    }
}