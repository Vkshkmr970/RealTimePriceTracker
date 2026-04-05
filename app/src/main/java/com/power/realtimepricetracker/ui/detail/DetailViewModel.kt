package com.power.realtimepricetracker.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.power.realtimepricetracker.data.model.PriceRecord
import com.power.realtimepricetracker.data.model.PriceUpdate
import com.power.realtimepricetracker.data.model.toPriceRecord
import com.power.realtimepricetracker.data.websocket.ConnectionState
import com.power.realtimepricetracker.data.websocket.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val record: PriceRecord? = null,
    val connectionState: ConnectionState = ConnectionState.Disconnected
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, private val repository: PriceRepository
) : ViewModel() {
    val symbol: String = checkNotNull(savedStateHandle["symbol"])

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        repository.getLastPrice(symbol)?.let { seedPrice ->
            val seed = PriceUpdate(symbol = symbol, price = seedPrice, timestamp = System.currentTimeMillis())
            _uiState.update { it.copy(record = seed.toPriceRecord(previous = null)) }
        }

        viewModelScope.launch {
            repository.connectionState.collect { state ->
                _uiState.update { it.copy(connectionState = state) }
            }
        }

        viewModelScope.launch {
            repository.priceUpdates.filter { it.symbol == symbol }.collect { update ->
                _uiState.update { state ->
                    state.copy(record = update.toPriceRecord(previous = state.record))
                }
            }
        }
    }

}