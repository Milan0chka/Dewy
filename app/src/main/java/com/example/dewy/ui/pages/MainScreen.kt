package com.example.dewy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.dewy.R
import com.example.dewy.viewmodels.MainViewModel
import com.example.dewy.viewmodels.Streak
import com.example.dewy.viewmodels.Tip


@Composable
fun MainPage(
    navHostController: NavHostController,
    mainViewModel: MainViewModel
){
    val tip by mainViewModel.tip.collectAsState()
    val streak by mainViewModel.streak.collectAsState()


    MainScaffold(
        navHostController
    ) {
        Column{
            StreakCard(streak)
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .weight(1f))
            tip?.let { TipCard(it) }
        }
    }
}

@Composable
fun StreakCard(streak: Streak){
    Card(
        modifier = Modifier
            .size(400.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(Brush.radialGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                    center = Offset.Unspecified,
                    radius = 500f
                )),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Streak: ${streak.num} ${if (streak.num == 1) "day" else "days"}!",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Image(
                painter = painterResource(streak.image),
                contentDescription = "Streak drop",
                modifier = Modifier.size(270.dp)
            )
            Text(
                text = streak.msg,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight(600)
            )
        }
    }
}

@Composable
fun TipCard(tip: Tip){
    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Category: ${tip.category}",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tip of the day:\n${tip.content}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight(600),
                textAlign = TextAlign.Center
            )
        }
    }
}