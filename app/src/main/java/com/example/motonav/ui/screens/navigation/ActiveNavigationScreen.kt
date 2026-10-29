package com.example.motonav.ui.screens.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motonav.navigation.Screen
import com.example.motonav.ui.theme.ErrorRed
import com.example.motonav.ui.theme.MotoCardBackground
import com.example.motonav.ui.theme.MotoDarkBackground
import com.example.motonav.ui.theme.MotoOrange
import com.example.motonav.ui.theme.MotoSurfaceVariant
import com.example.motonav.ui.theme.TextPrimary
import com.example.motonav.ui.theme.TextSecondary
import com.example.motonav.viewmodel.NavigationViewModel

@Composable
fun ActiveNavigationScreen(
    navController: NavController,
    viewModel: NavigationViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoDarkBackground)
    ) {
        // Simulated GPS Nav Map Canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MotoSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CompassCalibration,
                    contentDescription = null,
                    tint = MotoOrange.copy(alpha = 0.4f),
                    modifier = Modifier.size(160.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Live Navigation In Progress...", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Tracking motorcycle GPS heading: 240° SW", fontSize = 12.sp, color = TextSecondary)
            }
        }

        // Top Guidance Banner Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            colors = CardDefaults.cardColors(containerColor = MotoCardBackground.copy(alpha = 0.95f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(MotoOrange, shape = RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.TurnRight, contentDescription = "Turn Right", tint = TextPrimary, modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = state.distanceToNextTurn, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MotoOrange)
                    Text(text = state.nextInstruction, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, lineHeight = 18.sp)
                }
            }
        }

        // Side Control Buttons (Mute audio, Recenter map, Steps list)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MotoCardBackground, shape = CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { viewModel.toggleMute() }) {
                    Icon(
                        imageVector = if (state.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Mute",
                        tint = TextPrimary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MotoCardBackground, shape = CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { navController.navigate(Screen.NavigationDetails.route) }) {
                    Icon(Icons.Default.List, contentDescription = "Maneuvers List", tint = TextPrimary)
                }
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MotoOrange, shape = CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Recenter", tint = TextPrimary)
                }
            }
        }

        // Speedometer Widget Floating Card
        Box(
            modifier = Modifier
                .padding(start = 16.dp, bottom = 110.dp)
                .align(Alignment.BottomStart)
                .background(MotoCardBackground.copy(alpha = 0.95f), shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${state.currentSpeedKmh}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "KM/H", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MotoOrange)
            }
        }

        // Bottom Navigation Bar with ETA & End Ride Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            colors = CardDefaults.cardColors(containerColor = MotoCardBackground),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "${state.remainingMinutes} min • ${state.remainingDistanceKm} km", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "ETA ${state.etaString}", fontSize = 13.sp, color = TextSecondary)
                }

                Button(
                    onClick = {
                        viewModel.stopNavigation()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "End Ride", tint = TextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "End Ride", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }
    }
}
