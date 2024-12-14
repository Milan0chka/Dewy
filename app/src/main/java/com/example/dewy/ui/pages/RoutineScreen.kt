package com.example.dewy.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dewy.R
import com.example.dewy.Screen
import com.example.dewy.data.models.Routine
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
    val morningRoutine by routineViewModel.morningRoutine.collectAsState()
    val eveningRoutine by routineViewModel.eveningRoutine.collectAsState()

    LaunchedEffect(Unit) {
        if (morningRoutine == null && eveningRoutine == null)
            routineViewModel.fetchRoutines()
    }
    if (morningRoutine == null && eveningRoutine == null) {
        LoadingSpinner()
//    } else if (routines!!.isEmpty()){
//        NoRoutineFound(navController)
//    }
    }   else {
        UserRoutines(navController, morningRoutine, eveningRoutine)
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

@Composable
fun UserRoutines(
    navController: NavController,
    morningRoutine: Routine?,
    eveningRoutine: Routine?
){
    Text(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        text = "Choose which routine to perform now:",
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f),
            contentAlignment = Alignment.BottomCenter
        ){
            if(morningRoutine == null)
                AddRoutineButton(
                    navController,
                    "Morning",
                    Color.Yellow
                )
            else
                RoutineCard(
                    navController,
                    morningRoutine
                )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f),
            contentAlignment = Alignment.TopCenter
        ){
            if(eveningRoutine == null)
                AddRoutineButton(
                    navController,
                    "Evening",
                    Color.Yellow
                )
            else
                RoutineCard(
                    navController,
                    eveningRoutine
                )
        }
    }
}

@Composable
fun RoutineCard(
    navController: NavController,
    routine: Routine
) {

    data class RoutineTheme(
        val cardColor: Color,
        val textColor: Color,
        val gradientStartColor: Color,
        val startButtonColor: Color,
        val rebuildButtonColor: Color,
        val icon: Painter
    )

    val routineTheme = if (routine.type == "Morning") {
        RoutineTheme(
            cardColor = Color(0xFFFFE0B3),
            textColor = Color(0xFFDA5B00),
            gradientStartColor = Color(0xFFFFCB14),
            startButtonColor = Color(0xFFDA5B00),
            rebuildButtonColor = Color(0xFFFFB355),
            icon = painterResource(R.drawable.icon_day)
        )
    } else {
        RoutineTheme(
            cardColor = Color(0xFF282A72),
            textColor = Color(0xFFBFBFFF),
            gradientStartColor = Color(0xFF5858BB),
            startButtonColor = Color(0xFFBFBFFF),
            rebuildButtonColor = Color(0xFF484A92),
            icon = painterResource(R.drawable.icon_night)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = routineTheme.cardColor,
            contentColor = routineTheme.textColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            routineTheme.gradientStartColor,
                            routineTheme.cardColor
                        ),
                        center = Offset(250f, 400f),
                        radius = 350f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "${routine.type} Routine",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.55f),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            painter = routineTheme.icon,
                            contentDescription = if (routine.type == "Morning") "Morning Icon" else "Night Icon",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom)
                    ) {
                        Text(
                            text = "${routine.days[0].steps.size} steps",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                // navController.navigate("start_routine/${type}")
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = routineTheme.startButtonColor,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                                Text(
                                    text = "Start",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = routineTheme.cardColor
                                )
                        }

                        Button(
                            onClick = {
                                // Handle navigation to the routine builder
                                navController.navigate("routine_builder/${routine.type}")
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = routineTheme.rebuildButtonColor,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Rebuild",
                                style = MaterialTheme.typography.bodyLarge,
                                color = routineTheme.textColor
                            )
                        }
                    }
                }

            }
        }
    }
}


@Composable
fun AddRoutineButton(
    navController: NavController,
    type: String,
    color: Color
){

}