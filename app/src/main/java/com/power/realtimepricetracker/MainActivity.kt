package com.power.realtimepricetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.power.realtimepricetracker.navigation.AppNavigation
import com.power.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RealTimePriceTrackerTheme {
                AppNavigation()
            }
        }
    }
}
