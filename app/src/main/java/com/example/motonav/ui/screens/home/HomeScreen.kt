package com.example.motonav.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motonav.navigation.Screen
import com.example.motonav.ui.theme.MotoCardBackground
import com.example.motonav.ui.theme.MotoDarkBackground
import com.example.motonav.ui.theme.MotoOrange
import com.example.motonav.ui.theme.ScenicGreen
import com.example.motonav.ui.theme.TextPrimary
import com.example.motonav.ui.theme.TextSecondary
import com.example.motonav.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome Rider 🏍️",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = state.startLocation,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MotoCardBackground, shape = RoundedCornerShape(12.dp))
                    .clickable { navController.navigate(Screen.RiderProfile.route) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.TwoWheeler, contentDescription = "Profile", tint = MotoOrange)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Destination Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Screen.DestinationSearch.route) }
        ) {
            OutlinedTextField(
                value = state.destinationQuery,
                onValueChange = { viewModel.onDestinationQueryChanged(it) },
                placeholder = { Text("Where do you want to ride today?", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MotoOrange) },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = MotoCardBackground,
                    disabledBorderColor = MotoCardBackground,
                    disabledTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weather banner card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MotoCardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Thermostat, contentDescription = null, tint = MotoOrange, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Riding Forecast", fontSize = 12.sp, color = TextSecondary)
                    Text(text = state.weatherCondition, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Actions Grid
        Text(text = "Quick Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionItem(
                modifier = Modifier.weight(1f),
                title = "Map View",
                icon = Icons.Default.Map,
                onClick = { navController.navigate(Screen.Map.route) }
            )
            QuickActionItem(
                modifier = Modifier.weight(1f),
                title = "Route Plan",
                icon = Icons.Default.AltRoute,
                onClick = { navController.navigate(Screen.RouteComparison.route) }
            )
            QuickActionItem(
                modifier = Modifier.weight(1f),
                title = "Fuel Calc",
                icon = Icons.Default.LocalGasStation,
                onClick = { navController.navigate(Screen.FuelCalculator.route) }
            )
            QuickActionItem(
                modifier = Modifier.weight(1f),
                title = "Garage",
                icon = Icons.Default.TwoWheeler,
                onClick = { navController.navigate(Screen.BikeGarage.route) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recommended Scenic Route Card
        state.recommendedRoute?.let { route ->
            Text(text = "Recommended Ride", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.ScenicRouteDetails.route) },
                colors = CardDefaults.cardColors(containerColor = MotoCardBackground),
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
                                .background(ScenicGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = "SCENIC TWISTIES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ScenicGreen)
                        }
                        Text(text = "${route.distanceKm} km • ${route.durationMinutes} mins", fontSize = 13.sp, color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = route.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = route.description, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Twisty Rating: ${route.twistyScore}/10 🏍️",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MotoOrange
                        )

                        Box(
                            modifier = Modifier
                                .background(MotoOrange, shape = RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Start Ride", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nearby Scenic Spots Row
        Text(text = "Nearby Scenic Places", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.nearbyPlaces) { place ->
                Card(
                    modifier = Modifier
                        .width(220.dp)
                        .clickable { navController.navigate(Screen.ScenicPlaceDetails.createRoute(place.id)) },
                    colors = CardDefaults.cardColors(containerColor = MotoCardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = place.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = place.category, fontSize = 12.sp, color = MotoOrange)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = MotoOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${place.rating} (${place.reviewCount})", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "${place.distanceKm} km", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MotoCardBackground),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = MotoOrange, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
    }
}
