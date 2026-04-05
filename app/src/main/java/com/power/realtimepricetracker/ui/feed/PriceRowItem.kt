package com.power.realtimepricetracker.ui.feed

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.power.realtimepricetracker.data.model.PriceDirection
import com.power.realtimepricetracker.data.model.PriceRecord
import com.power.realtimepricetracker.ui.theme.PriceDown
import com.power.realtimepricetracker.ui.theme.PriceUp
import kotlinx.coroutines.delay

@Composable
fun PriceRowItem(
    record: PriceRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var flashActive by remember { mutableStateOf(false) }

    // Re-run every time flashTrigger increments (i.e. each new price update)
    LaunchedEffect(record.flashTrigger) {
        if (record.direction != PriceDirection.UNCHANGED) {
            flashActive = true
            delay(1000L)
            flashActive = false
        }
    }

    val flashTargetColor = when (record.direction) {
        PriceDirection.UP -> PriceUp.copy(alpha = 0.22f)
        PriceDirection.DOWN -> PriceDown.copy(alpha = 0.22f)
        PriceDirection.UNCHANGED -> Color.Transparent
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (flashActive) flashTargetColor else Color.Transparent,
        animationSpec = if (flashActive) {
            tween(durationMillis = 60, easing = LinearEasing)
        } else {
            tween(durationMillis = 900, easing = LinearOutSlowInEasing)
        },
        label = "rowFlash"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = record.symbol,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = record.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            val dirColor = when (record.direction) {
                PriceDirection.UP -> PriceUp
                PriceDirection.DOWN -> PriceDown
                PriceDirection.UNCHANGED -> MaterialTheme.colorScheme.onSurface
            }
            val arrow = when (record.direction) {
                PriceDirection.UP -> "↑"
                PriceDirection.DOWN -> "↓"
                PriceDirection.UNCHANGED -> "—"
            }
            Text(
                text = "$${"%.2f".format(record.price)}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = arrow,
                style = MaterialTheme.typography.labelSmall,
                color = dirColor
            )
        }
    }
}