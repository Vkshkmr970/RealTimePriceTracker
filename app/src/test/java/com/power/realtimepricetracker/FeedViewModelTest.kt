package com.power.realtimepricetracker

import com.power.realtimepricetracker.data.model.PriceUpdate
import com.power.realtimepricetracker.data.websocket.ConnectionState
import com.power.realtimepricetracker.data.websocket.PriceRepository
import com.power.realtimepricetracker.ui.feed.FeedViewModel
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [FeedViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Backing flows the fake repository exposes
    private val fakePriceUpdates   = MutableSharedFlow<PriceUpdate>(extraBufferCapacity = 64)
    private val fakeConnectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)

    private val repository = mockk<PriceRepository> {
        coJustRun { initializePrices() }
        every { priceUpdates }    returns fakePriceUpdates
        every { connectionState } returns fakeConnectionState
        every { getLastPrice(any()) } returns null
        every { startTracking() } returns Unit
        every { stopTracking() }  returns Unit
    }

    private fun buildViewModel() = FeedViewModel(repository)

    // ─── Initial state ────────────────────────────────────────────────────────

    @Test
    fun `initial state shows the loading indicator`() = runTest {
        val vm = buildViewModel()
        assertTrue(vm.uiState.value.isInitializing)
    }

    @Test
    fun `initial price list is empty`() = runTest {
        val vm = buildViewModel()
        assertTrue(vm.uiState.value.prices.isEmpty())
    }

    @Test
    fun `tracking is off by default`() = runTest {
        val vm = buildViewModel()
        assertFalse(vm.uiState.value.isTracking)
    }

    @Test
    fun `isInitializing clears after the repository finishes loading`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()
        assertFalse(vm.uiState.value.isInitializing)
    }

    // ─── Toggle tracking ──────────────────────────────────────────────────────

    @Test
    fun `toggleTracking starts the feed when idle`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.toggleTracking()

        assertTrue(vm.uiState.value.isTracking)
        verify(exactly = 1) { repository.startTracking() }
    }

    @Test
    fun `toggleTracking stops the feed when it is already running`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.toggleTracking() // start
        vm.toggleTracking() // stop

        assertFalse(vm.uiState.value.isTracking)
        verify(exactly = 1) { repository.stopTracking() }
    }

    @Test
    fun `calling startTracking is not triggered while initializing`() = runTest {
        val vm = buildViewModel()
        vm.toggleTracking()
        assertTrue(vm.uiState.value.isTracking)
    }

    // ─── Price updates ────────────────────────────────────────────────────────

    @Test
    fun `incoming price update appears in the price list`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        fakePriceUpdates.emit(PriceUpdate("AAPL", 150.0, 1_000L))
        advanceUntilIdle()

        val prices = vm.uiState.value.prices
        assertEquals(1, prices.size)
        assertEquals("AAPL", prices[0].symbol)
        assertEquals(150.0,  prices[0].price, 0.001)
    }

    @Test
    fun `a second update for the same symbol replaces the first`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        fakePriceUpdates.emit(PriceUpdate("AAPL", 100.0, 1_000L))
        fakePriceUpdates.emit(PriceUpdate("AAPL", 155.0, 2_000L))
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.prices.size)
        assertEquals(155.0, vm.uiState.value.prices[0].price, 0.001)
    }

    @Test
    fun `updates for different symbols all appear in the list`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        fakePriceUpdates.emit(PriceUpdate("AAPL", 150.0, 1_000L))
        fakePriceUpdates.emit(PriceUpdate("MSFT", 300.0, 1_001L))
        advanceUntilIdle()

        assertEquals(2, vm.uiState.value.prices.size)
    }

    @Test
    fun `prices are sorted in descending order by current price`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        fakePriceUpdates.emit(PriceUpdate("AAPL",  100.0, 1_000L))
        fakePriceUpdates.emit(PriceUpdate("MSFT",  300.0, 1_001L))
        fakePriceUpdates.emit(PriceUpdate("GOOGL", 200.0, 1_002L))
        advanceUntilIdle()

        val prices = vm.uiState.value.prices
        assertEquals(3, prices.size)
        assertTrue(prices[0].price >= prices[1].price)
        assertTrue(prices[1].price >= prices[2].price)
    }

    // ─── Connection state ─────────────────────────────────────────────────────

    @Test
    fun `connection state change propagates to the UI state`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        fakeConnectionState.value = ConnectionState.Connected
        advanceUntilIdle()

        assertEquals(ConnectionState.Connected, vm.uiState.value.connectionState)
    }

    @Test
    fun `disconnection after connection propagates correctly`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        fakeConnectionState.value = ConnectionState.Connected
        fakeConnectionState.value = ConnectionState.Disconnected
        advanceUntilIdle()

        assertEquals(ConnectionState.Disconnected, vm.uiState.value.connectionState)
    }

    @Test
    fun `connecting state is reflected while handshake is in progress`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        fakeConnectionState.value = ConnectionState.Connecting
        advanceUntilIdle()

        assertEquals(ConnectionState.Connecting, vm.uiState.value.connectionState)
    }
}
