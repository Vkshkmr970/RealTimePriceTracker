package com.power.realtimepricetracker

import androidx.lifecycle.SavedStateHandle
import com.power.realtimepricetracker.data.model.PriceDirection
import com.power.realtimepricetracker.data.model.PriceUpdate
import com.power.realtimepricetracker.data.websocket.ConnectionState
import com.power.realtimepricetracker.data.websocket.PriceRepository
import com.power.realtimepricetracker.ui.detail.DetailViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [DetailViewModel].
 *
 * Key behaviours verified:
 *  - Symbol binding via [SavedStateHandle]
 *  - Immediate seed price population from [PriceRepository.getLastPrice]
 *  - Filtering — only updates whose symbol matches are processed
 *  - Direction calculation across successive ticks
 *  - Connection state forwarding
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakePriceUpdates    = MutableSharedFlow<PriceUpdate>(extraBufferCapacity = 64)
    private val fakeConnectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)

    private val repository = mockk<PriceRepository> {
        every { priceUpdates }    returns fakePriceUpdates
        every { connectionState } returns fakeConnectionState
        every { getLastPrice(any()) } returns null
    }

    private fun buildViewModel(symbol: String = "AAPL") = DetailViewModel(
        savedStateHandle = SavedStateHandle(mapOf("symbol" to symbol)),
        repository       = repository
    )

    // ─── Symbol binding ───────────────────────────────────────────────────────

    @Test
    fun `symbol property reflects the value stored in SavedStateHandle`() {
        assertEquals("MSFT", buildViewModel("MSFT").symbol)
    }

    // ─── Seed price (getLastPrice) ────────────────────────────────────────────

    @Test
    fun `record is null on start when no cached price is available`() {
        assertNull(buildViewModel().uiState.value.record)
    }

    @Test
    fun `record is populated immediately when a cached price exists`() {
        every { repository.getLastPrice("AAPL") } returns 175.0

        val record = buildViewModel("AAPL").uiState.value.record

        assertNotNull(record)
        assertEquals(175.0, record!!.price, 0.001)
    }

    @Test
    fun `seeded record has UNCHANGED direction because there is no prior tick`() {
        every { repository.getLastPrice("AAPL") } returns 175.0

        val record = buildViewModel("AAPL").uiState.value.record!!
        assertEquals(PriceDirection.UNCHANGED, record.direction)
    }

    @Test
    fun `seeded record prevPrice equals the seed price`() {
        every { repository.getLastPrice("AAPL") } returns 200.0

        val record = buildViewModel("AAPL").uiState.value.record!!
        assertEquals(200.0, record.prevPrice, 0.001)
    }

    // ─── Price filtering ──────────────────────────────────────────────────────

    @Test
    fun `update for a different symbol does not affect the record`() = runTest {
        val vm = buildViewModel("AAPL")
        advanceUntilIdle() // start the collect coroutine before emitting

        fakePriceUpdates.emit(PriceUpdate("MSFT", 999.0, 1_000L))
        advanceUntilIdle()

        assertNull(vm.uiState.value.record)
    }

    @Test
    fun `update for the watched symbol updates the record`() = runTest {
        val vm = buildViewModel("AAPL")
        advanceUntilIdle() // start the collect coroutine before emitting

        fakePriceUpdates.emit(PriceUpdate("AAPL", 200.0, 1_000L))
        advanceUntilIdle()

        assertNotNull(vm.uiState.value.record)
        assertEquals(200.0, vm.uiState.value.record!!.price, 0.001)
    }

    @Test
    fun `updates are ignored for all symbols except the watched one`() = runTest {
        val vm = buildViewModel("AAPL")
        advanceUntilIdle() // start the collect coroutine before emitting

        fakePriceUpdates.emit(PriceUpdate("MSFT",  300.0, 1_000L))
        fakePriceUpdates.emit(PriceUpdate("GOOGL", 200.0, 1_001L))
        fakePriceUpdates.emit(PriceUpdate("AAPL",  150.0, 1_002L))
        advanceUntilIdle()

        assertEquals(150.0, vm.uiState.value.record!!.price, 0.001)
    }

    // ─── Direction across ticks ───────────────────────────────────────────────

    @Test
    fun `direction is UP when the new price exceeds the seeded price`() = runTest {
        every { repository.getLastPrice("AAPL") } returns 150.0
        val vm = buildViewModel("AAPL")
        advanceUntilIdle() // start the collect coroutine before emitting

        fakePriceUpdates.emit(PriceUpdate("AAPL", 160.0, 1_000L))
        advanceUntilIdle()

        assertEquals(PriceDirection.UP, vm.uiState.value.record!!.direction)
    }

    @Test
    fun `direction is DOWN when the new price falls below the seeded price`() = runTest {
        every { repository.getLastPrice("AAPL") } returns 150.0
        val vm = buildViewModel("AAPL")
        advanceUntilIdle() // start the collect coroutine before emitting

        fakePriceUpdates.emit(PriceUpdate("AAPL", 140.0, 1_000L))
        advanceUntilIdle()

        assertEquals(PriceDirection.DOWN, vm.uiState.value.record!!.direction)
    }

    @Test
    fun `direction is UNCHANGED when price stays flat across two ticks`() = runTest {
        val vm = buildViewModel("AAPL")
        advanceUntilIdle() // start the collect coroutine before emitting

        fakePriceUpdates.emit(PriceUpdate("AAPL", 150.0, 1_000L))
        fakePriceUpdates.emit(PriceUpdate("AAPL", 150.0, 2_000L))
        advanceUntilIdle()

        assertEquals(PriceDirection.UNCHANGED, vm.uiState.value.record!!.direction)
    }

    // ─── Connection state ─────────────────────────────────────────────────────

    @Test
    fun `connection state changes are forwarded to the UI state`() = runTest {
        val vm = buildViewModel()

        fakeConnectionState.value = ConnectionState.Connected
        advanceUntilIdle()

        assertEquals(ConnectionState.Connected, vm.uiState.value.connectionState)
    }

    @Test
    fun `disconnection after connection is reflected correctly`() = runTest {
        val vm = buildViewModel()

        fakeConnectionState.value = ConnectionState.Connected
        fakeConnectionState.value = ConnectionState.Disconnected
        advanceUntilIdle()

        assertEquals(ConnectionState.Disconnected, vm.uiState.value.connectionState)
    }
}
