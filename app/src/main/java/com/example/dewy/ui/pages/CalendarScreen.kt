package com.example.dewy.ui.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController


@Composable
fun CalendarPage(
    navController: NavController,
){
    InfoCard(){
        Text("Calendar")
    }
}
