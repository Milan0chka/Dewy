package com.example.dewy.ui.pages

import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.dewy.data.models.Streak
import com.example.dewy.data.models.Tip
import com.example.dewy.viewmodels.StreakViewModel
import com.example.dewy.viewmodels.TipViewModel


@Composable
fun MainPage(
    navHostController: NavHostController,
    streakViewModel: StreakViewModel,
    tipViewModel: TipViewModel
){
    val streak by streakViewModel.streak.collectAsState()
    val tip by tipViewModel.tip.collectAsState()

    MainScaffold(navHostController) {
        Column(
            modifier = Modifier.padding(16.dp)
        ){
            StreakCard(streak, streakViewModel)

            Spacer(modifier = Modifier.fillMaxWidth().weight(1f))

            TipCard(tip, tipViewModel)
        }
    }
}

@Composable
fun StreakCard(
    streak: Streak?,
    streakViewModel: StreakViewModel
){
    val streakDanger by streakViewModel.streakDanger.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit){
        streakViewModel.checkStreakStatus()
    }

        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            if(streak != null){
                Box{
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(Brush.radialGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.primaryContainer
                                ),
                                center = Offset.Unspecified,
                                radius = 500f
                            )),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
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
                            fontWeight = FontWeight(600),
                            textAlign = TextAlign.Center
                        )
                    }

                    if(streakDanger){
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Streak expiring",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
                                .padding(8.dp)
                                .clickable {
                                    Toast.makeText(
                                        context,
                                        "Your streak is in danger!\nFinish at least 1 routine to keep it!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        )
                    }
                }

            } else{
                LoadingSpinner(MaterialTheme.colorScheme.onPrimaryContainer)
            }

        }

}

@Composable
fun TipCard(
    tip: Tip?,
    tipViewModel: TipViewModel
){
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (tip != null){
                Box{
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(vertical = 16.dp)
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = "Skin concern: ${tip.category}",
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Random tip:\n${tip.content}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight(600),
                            textAlign = TextAlign.Center
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Streak expiring",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.surfaceDim, RoundedCornerShape(12.dp))
                            .padding(4.dp)
                            .size(20.dp)
                            .clickable {
                                tipViewModel.fetchRandomTip()
                            }
                    )
                }

            } else {
                LoadingSpinner()
            }
        }
}

@Composable
fun LoadingSpinner(
    color: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = color)
    }
}
