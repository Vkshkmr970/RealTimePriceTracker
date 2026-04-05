package com.power.realtimepricetracker.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.power.realtimepricetracker.data.websocket.ConnectionState
import com.power.realtimepricetracker.ui.theme.PriceDown
import com.power.realtimepricetracker.ui.theme.PriceUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onSymbolClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            val dotColor: Color = when (uiState.connectionState) {
                ConnectionState.Connected -> PriceUp
                ConnectionState.Connecting -> Color(0xFFFFA000)
                ConnectionState.Disconnected -> PriceDown
            }
            TopAppBar(
                // Left: connection status indicator
                navigationIcon = {
                    Row(
                        modifier = Modifier.padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(dotColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (uiState.connectionState) {
                                ConnectionState.Connected -> "Live"
                                ConnectionState.Connecting -> "Connecting…"
                                ConnectionState.Disconnected -> "Off"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = dotColor
                        )
                    }
                },
                title = { Text("Price Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                // Right: start / stop toggle — disabled while initializing
                actions = {
                    TextButton(
                        onClick = viewModel::toggleTracking,
                        enabled = !uiState.isInitializing
                    ) {
                        Text(if (uiState.isTracking) "Stop" else "Start")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            // Show loader while seed prices are being prepared
            uiState.isInitializing -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading market data…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Empty state — initialized but feed not started yet
            uiState.prices.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.isTracking) "Waiting for data…" else "Tap Start to begin",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Price list
            else -> {
                LazyColumn(contentPadding = innerPadding) {
                    items(
                        items = uiState.prices,
                        key = { it.symbol }
                    ) { record ->
                        PriceRowItem(
                            record = record,
                            onClick = { onSymbolClick(record.symbol) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}
