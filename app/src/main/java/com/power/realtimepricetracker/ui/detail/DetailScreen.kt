package com.power.realtimepricetracker.ui.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.power.realtimepricetracker.data.model.PriceDirection
import com.power.realtimepricetracker.ui.theme.PriceDown
import com.power.realtimepricetracker.ui.theme.PriceUp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val record = uiState.record

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(record?.symbol ?: viewModel.symbol) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (record == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        } else {
            var flashActive by remember { mutableStateOf(false) }

            LaunchedEffect(record.flashTrigger) {
                if (record.direction != PriceDirection.UNCHANGED) {
                    flashActive = true
                    delay(1000L)
                    flashActive = false
                }
            }

            val flashTarget = when (record.direction) {
                PriceDirection.UP -> PriceUp.copy(alpha = 0.15f)
                PriceDirection.DOWN -> PriceDown.copy(alpha = 0.15f)
                PriceDirection.UNCHANGED -> Color.Transparent
            }
            val bgColor by animateColorAsState(
                targetValue = if (flashActive) flashTarget else Color.Transparent,
                animationSpec = if (flashActive) {
                    tween(durationMillis = 60, easing = LinearEasing)
                } else {
                    tween(durationMillis = 900, easing = LinearOutSlowInEasing)
                },
                label = "detailFlash"
            )

            val priceColor = when (record.direction) {
                PriceDirection.UP -> PriceUp
                PriceDirection.DOWN -> PriceDown
                PriceDirection.UNCHANGED -> MaterialTheme.colorScheme.onSurface
            }
            val arrow = when (record.direction) {
                PriceDirection.UP -> "↑"
                PriceDirection.DOWN -> "↓"
                PriceDirection.UNCHANGED -> "—"
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Company name subtitle
                Text(
                    text = record.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Price card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Current Price",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "$${"%.2f".format(record.price)}",
                                style = MaterialTheme.typography.displaySmall,
                                color = priceColor
                            )
                            Text(
                                text = arrow,
                                style = MaterialTheme.typography.headlineMedium,
                                color = priceColor
                            )
                        }
                        Text(
                            text = "Prev: $${"%.2f".format(record.prevPrice)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(20.dp))

                // About section
                Text(
                    text = "About ${record.symbol}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = record.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyMedium.fontSize * 1.6
                )
            }
        }
    }
}