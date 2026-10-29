package com.example.motonav.ui.screens.navigation

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.TurnLeft
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material.icons.filled.Straight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motonav.ui.theme.MotoCardBackground
import com.example.motonav.ui.theme.MotoDarkBackground
import com.example.motonav.ui.theme.MotoOrange
import com.example.motonav.ui.theme.TextPrimary
import com.example.motonav.ui.theme.TextSecondary

data class ManeuverStep(
    val distance: String,
    val instruction: String,
    val roadName: String,
    val turnType: String
)

@Composable
fun NavigationDetailsScreen(navController: NavController) {
    val steps = listOf(
        ManeuverStep("300 m", "Turn right onto Amby Valley Hairpin Road", "Amby Valley Rd", "RIGHT"),
        ManeuverStep("4.2 km", "Continue straight through Lonavala Pass", "NH-48 Scenic Bypass", "STRAIGHT"),
        ManeuverStep("1.8 km", "Bear left towards Khandala Viewpoint Bend", "Old Mumbai Highway", "LEFT"),
        ManeuverStep("12.5 km", "Keep right at Tiger Point Junction", "Tiger Point Connector", "RIGHT"),
        ManeuverStep("19.6 km", "Arrive at Mahabaleshwar Peak Viewpoint", "Peak Summit Rd", "ARRIVE")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoDarkBackground)
            .padding(16.dp)
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
            Text(text = "Turn-by-Turn Directions", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(steps) { step ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MotoCardBackground),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (step.turnType) {
                                "RIGHT" -> Icons.Default.TurnRight
                                "LEFT" -> Icons.Default.TurnLeft
                                else -> Icons.Default.Straight
                            },
                            contentDescription = null,
                            tint = MotoOrange
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = step.instruction, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = step.roadName, fontSize = 12.sp, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = step.distance, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MotoOrange)
                    }
                }
            }
        }
    }
}
