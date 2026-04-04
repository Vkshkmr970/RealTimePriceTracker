package com.power.realtimepricetracker.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.power.realtimepricetracker.data.model.PriceDirection
import com.power.realtimepricetracker.data.model.PriceRecord
import com.power.realtimepricetracker.data.model.StockSymbol
import com.power.realtimepricetracker.data.model.toPriceRecord
import com.power.realtimepricetracker.data.websocket.ConnectionState
import com.power.realtimepricetracker.data.websocket.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedUiState(
    val prices: List<PriceRecord> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val isTracking: Boolean = false,
    val isInitializing: Boolean = true
)


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: PriceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val priceMap = mutableMapOf<String, PriceRecord>()

    init {
        // Step 1: fetch initial prices before showing anything
        viewModelScope.launch {
            repository.initializePrices()
            _uiState.update { it.copy(isInitializing = false) }
        }

        // Step 2: keep connection state in sync
        viewModelScope.launch {
            repository.connectionState.collect { state ->
                _uiState.update { it.copy(connectionState = state) }
            }
        }

        // Step 3: process incoming price updates from the WebSocket echo
        viewModelScope.launch {
            repository.priceUpdates.collect { update ->
                val record = update.toPriceRecord(previous = priceMap[update.symbol])
                priceMap[update.symbol] = record
                _uiState.update { state ->
                    state.copy(
                        prices = priceMap.values.sortedByDescending { it.price }
                    )
                }
            }
        }
    }


}