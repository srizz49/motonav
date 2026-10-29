package com.example.motonav.ui.screens.explore

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.motonav.navigation.Screen
import com.example.motonav.ui.theme.MotoCardBackground
import com.example.motonav.ui.theme.MotoDarkBackground
import com.example.motonav.ui.theme.MotoOrange
import com.example.motonav.ui.theme.TextPrimary
import com.example.motonav.ui.theme.TextSecondary
import com.example.motonav.viewmodel.ExploreViewModel

@Composable
fun ScenicPlaceDetailsScreen(
    navController: NavController,
    viewModel: ExploreViewModel,
    placeId: String
) {
    val place = viewModel.getPlaceById(placeId)

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Scenic Place Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            place?.let { p ->
                IconButton(onClick = { viewModel.toggleSavePlace(p.id) }) {
                    Icon(
                        imageVector = if (p.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = MotoOrange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            colors = CardDefaults.cardColors(containerColor = MotoCardBackground),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (place?.imageUrl != null) {
                    AsyncImage(
                        model = place.imageUrl,
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Terrain, contentDescription = null, tint = MotoOrange, modifier = Modifier.size(60.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MotoCardBackground),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = place?.name ?: "Scenic Spot", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = place?.category ?: "Viewpoint", fontSize = 13.sp, color = MotoOrange)

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = MotoOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${place?.rating ?: 4.8} (${place?.reviewCount ?: 200} reviews)", fontSize = 14.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "${place?.distanceKm ?: 48.0} km away", fontSize = 14.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = place?.description ?: "", fontSize = 14.sp, color = TextSecondary, lineHeight = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { navController.navigate(Screen.RouteComparison.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MotoOrange),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Navigation, contentDescription = null, tint = TextPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Plan Ride to Location", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}
