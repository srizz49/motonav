package com.example.motonav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.motonav.di.AppModule
import com.example.motonav.navigation.AppNavGraph
import com.example.motonav.ui.theme.MotonavTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize central dependency provider with application context
        AppModule.init(this.applicationContext)

        setContent {
            MotonavTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}
