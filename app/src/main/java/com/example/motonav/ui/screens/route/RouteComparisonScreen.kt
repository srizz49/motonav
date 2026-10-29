package com.example.motonav.ui.screens.route

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motonav.data.model.RouteInfo
import com.example.motonav.data.model.RouteType
import com.example.motonav.navigation.Screen
import com.example.motonav.ui.theme.FastestBlue
import com.example.motonav.ui.theme.MotoCardBackground
import com.example.motonav.ui.theme.MotoDarkBackground
import com.example.motonav.ui.theme.MotoOrange
import com.example.motonav.ui.theme.ScenicGreen
import com.example.motonav.ui.theme.TextPrimary
import com.example.motonav.ui.theme.TextSecondary
import com.example.motonav.viewmodel.RouteViewModel

@Composable
fun RouteComparisonScreen(
    navController: NavController,
    viewModel: RouteViewModel
) {
    val state by viewModel.uiState.collectAsState()

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
            Column {
                Text(text = "Route Comparison", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "${state.selectedStartLocation} ➔ ${state.selectedDestination}", fontSize = 12.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scenic Route Card
        RouteCard(
            route = state.scenicRoute,
            isSelected = state.selectedRouteType == RouteType.SCENIC,
            badgeColor = ScenicGreen,
            badgeText = "RECOMMENDED FOR RIDERS",
            onSelect = { viewModel.selectRouteType(RouteType.SCENIC) },
            onDetailsClick = { navController.navigate(Screen.ScenicRouteDetails.route) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fastest Route Card
        RouteCard(
            route = state.fastestRoute,
            isSelected = state.selectedRouteType == RouteType.FASTEST,
            badgeColor = FastestBlue,
            badgeText = "DIRECT HIGHWAY",
            onSelect = { viewModel.selectRouteType(RouteType.FASTEST) },
            onDetailsClick = { navController.navigate(Screen.FastestRouteDetails.route) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
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
            Text(
                text = "Proceed with ${if (state.selectedRouteType == RouteType.SCENIC) "Scenic" else "Fastest"} Route",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { navController.navigate(Screen.RouteStops.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Manage Waypoints & Stops (${state.waypoints.size})", fontSize = 14.sp, color = TextPrimary)
        }
    }
}

@Composable
private fun RouteCard(
    route: RouteInfo,
    isSelected: Boolean,
    badgeColor: Color,
    badgeText: String,
    onSelect: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MotoCardBackground else MotoCardBackground.copy(alpha = 0.6f)
        ),
        border = if (isSelected) BorderStroke(2.dp, MotoOrange) else null,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = badgeText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                }
                if (isSelected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = MotoOrange, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = route.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = route.roadConditions, fontSize = 13.sp, color = TextSecondary)

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
                    Text(text = "Est. Time", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "${route.durationMinutes} mins", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column {
                    Text(text = "Twisty Rating", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "${route.twistyScore}/10 🏍️", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MotoOrange)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "View Route Details & Waypoints ➔",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MotoOrange,
                modifier = Modifier.clickable { onDetailsClick() }
            )
        }
    }
}
