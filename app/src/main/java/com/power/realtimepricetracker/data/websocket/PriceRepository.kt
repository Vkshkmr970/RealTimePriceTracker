package com.power.realtimepricetracker.data.websocket

import com.power.realtimepricetracker.data.model.PriceUpdate
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class PriceRepository(private val manager: WebSocketManager) {

    val priceUpdates: SharedFlow<PriceUpdate>
        get() = manager.priceUpdates

    val connectionState: StateFlow<ConnectionState>
        get() = manager.connectionState

    suspend fun initializePrices() = manager.intializePrices()

    fun getLastPrice(symbol: String): Double? = manager.getLastPrice(symbol)

    fun startTracking() = manager.start()

    fun stopTracking() = manager.stop()

}