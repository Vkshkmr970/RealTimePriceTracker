package com.power.realtimepricetracker.data.websocket

import com.power.realtimepricetracker.data.model.PriceUpdate
import com.power.realtimepricetracker.data.model.StockSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.random.Random

sealed class ConnectionState {
    object Connected : ConnectionState()
    object Connecting : ConnectionState()
    object Disconnected : ConnectionState()
}

class WebSocketManager {

    private val client = OkHttpClient.Builder().pingInterval(20, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS).build()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _priceUpdates = MutableSharedFlow<PriceUpdate>(extraBufferCapacity = 64)

    val priceUpdates: SharedFlow<PriceUpdate> = _priceUpdates.asSharedFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var webSocket: WebSocket? = null
    private var tickerJob: Job? = null

    private val currentPrices: MutableMap<String, Double> = mutableMapOf()


    suspend fun intializePrices() {
        delay(1000L)
        StockSymbol.ALL.forEach { stockSymbols ->
            currentPrices[stockSymbols.ticker] = generateInitialPrice(stockSymbols.ticker)
        }
    }

    fun start() {
        if (_connectionState.value != ConnectionState.Disconnected) return
        _connectionState.value = ConnectionState.Connecting

        val request = Request.Builder().url("wss://ws.postman-echo.com/raw").build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _connectionState.value = ConnectionState.Connected
                startTickerLoop(webSocket )
            }


        })
    }

    private fun startTickerLoop(ws: WebSocket) {
        tickerJob = scope.launch {
            while (isActive) {
                StockSymbol.ALL.forEach { stock ->
                    if (!isActive) return@forEach

                    val price = nextPrice(stock.ticker)
                    val msg = Json.encodeToString(
                        PriceUpdate(
                            symbol = stock.ticker,
                            price = price,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    ws.send(msg)
                    delay(80L)
                }
                delay(2000L)
            }
        }
    }


    private fun nextPrice(symbol: String): Double {
        val current = currentPrices.getOrPut(symbol) { generateInitialPrice(symbol) }

        val swing = current * Random.nextDouble(-2.0, 2.0)
        val next = (current + swing).coerceAtLeast(0.01)
        val rounded = (next * 100).roundToInt() / 100.0

        currentPrices[symbol] = rounded
        return rounded
    }

    private fun generateInitialPrice(ticker: String): Double {
        val basePrice = (ticker.hashCode().absoluteValue % 500) + 50
        val variation = Random.nextDouble(-2.0, 2.0)
        val price = basePrice + variation
        return (price * 100).toInt() / 100.0
    }

    fun stop() {
        tickerJob?.cancel()
        tickerJob = null
        webSocket?.close(1000, "User stopped feed")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
    }
}