package com.example.motonav.ui.screens.map

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motonav.navigation.Screen
import com.example.motonav.ui.theme.FastestBlue
import com.example.motonav.ui.theme.MotoCardBackground
import com.example.motonav.ui.theme.MotoDarkBackground
import com.example.motonav.ui.theme.MotoOrange
import com.example.motonav.ui.theme.MotoSurfaceVariant
import com.example.motonav.ui.theme.ScenicGreen
import com.example.motonav.ui.theme.TextPrimary
import com.example.motonav.ui.theme.TextSecondary

@Composable
fun MapScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoDarkBackground)
    ) {
        // Simulated Interactive Map Canvas Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MotoSurfaceVariant)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CompassCalibration,
                    contentDescription = null,
                    tint = MotoOrange.copy(alpha = 0.3f),
                    modifier = Modifier.size(140.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Interactive Vector Map Preview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Text(
                    text = "Western Ghats Mountain Range • Active GPS Lat 18.52 / Long 73.85",
                    fontSize = 12.sp,
                    color = TextSecondary.copy(alpha = 0.7f)
                )
            }

            // Map Pins Overlay (Current location, scenic viewpoints, photo pins)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
                    .background(MotoOrange.copy(alpha = 0.2f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MotoOrange, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "My Location", tint = TextPrimary, modifier = Modifier.size(14.dp))
                }
            }

            // Pin 1: Scenic Pass
            Box(
                modifier = Modifier
                    .padding(top = 180.dp, start = 80.dp)
                    .background(ScenicGreen, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable { navController.navigate(Screen.ScenicPlaceDetails.createRoute("place_1")) }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Terrain, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Twisty Loop 🏔️", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }

            // Pin 2: Photo Map Marker
            Box(
                modifier = Modifier
                    .padding(top = 340.dp, end = 60.dp)
                    .align(Alignment.TopEnd)
                    .background(FastestBlue, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable { navController.navigate(Screen.PhotoMap.route) }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Photo Pin 📸", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }

        // Top Search Floating Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
                .clickable { navController.navigate(Screen.DestinationSearch.route) },
            colors = CardDefaults.cardColors(containerColor = MotoCardBackground.copy(alpha = 0.95f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = MotoOrange)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Search destination, scenic pass, viewpoint...", color = TextSecondary, fontSize = 14.sp)
            }
        }

        // Right side Map Control Buttons (Zoom in/out, Recenter)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MotoCardBackground, shape = CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {}) { Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = TextPrimary) }
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MotoCardBackground, shape = CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {}) { Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = TextPrimary) }
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MotoOrange, shape = CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {}) { Icon(Icons.Default.MyLocation, contentDescription = "Recenter", tint = TextPrimary) }
            }
        }

        // Bottom Action Card to Launch Route Comparison
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            colors = CardDefaults.cardColors(containerColor = MotoCardBackground),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MotoOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Destination: Mahabaleshwar Peak", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "142 km • 2 Route options calculated", fontSize = 13.sp, color = TextSecondary)

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { navController.navigate(Screen.RouteComparison.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MotoOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = TextPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Compare Scenic vs Fastest Route", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }
    }
}
