package com.example.motonav.ui.screens.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motonav.navigation.Screen
import com.example.motonav.ui.theme.FastestBlue
import com.example.motonav.ui.theme.MotoCardBackground
import com.example.motonav.ui.theme.MotoDarkBackground
import com.example.motonav.ui.theme.MotoOrange
import com.example.motonav.ui.theme.TextPrimary
import com.example.motonav.ui.theme.TextSecondary
import com.example.motonav.viewmodel.RouteViewModel

@Composable
fun FastestRouteDetailsScreen(
    navController: NavController,
    viewModel: RouteViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val route = state.fastestRoute

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Fastest Route Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MotoCardBackground),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = route.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = route.description, fontSize = 14.sp, color = TextSecondary, lineHeight = 20.sp)

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Distance", fontSize = 11.sp, color = TextSecondary)
                        Text(text = "${route.distanceKm} km", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column {
                        Text(text = "Est. Riding Time", fontSize = 11.sp, color = TextSecondary)
                        Text(text = "${route.durationMinutes} min", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FastestBlue)
                    }
                    Column {
                        Text(text = "Time Saved", fontSize = 11.sp, color = TextSecondary)
                        Text(text = "50 min faster ⚡", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FastestBlue)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Highway & Transit Information", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MotoCardBackground),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = FastestBlue)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = route.roadConditions, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { navController.navigate(Screen.PreRideSummary.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MotoOrange),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Navigation, contentDescription = null, tint = TextPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Select Fastest Route & Start", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}
