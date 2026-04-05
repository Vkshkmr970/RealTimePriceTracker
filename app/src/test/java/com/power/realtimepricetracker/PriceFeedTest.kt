package com.power.realtimepricetracker

import com.power.realtimepricetracker.data.model.PriceDirection
import com.power.realtimepricetracker.data.model.PriceUpdate
import com.power.realtimepricetracker.data.model.StockSymbol
import com.power.realtimepricetracker.data.model.toPriceRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure unit tests for the data-model layer.
 *
 * Covers:
 *  - [StockSymbol] catalogue integrity (count, uniqueness, completeness)
 *  - [toPriceRecord] extension: direction, flashTrigger, prevPrice, name/description lookup
 */
class PriceFeedTest {

    // ─── StockSymbol catalogue ────────────────────────────────────────────────

    @Test
    fun `ALL list contains exactly 25 symbols`() {
        assertEquals(25, StockSymbol.ALL.size)
    }

    @Test
    fun `no two symbols share the same ticker`() {
        val tickers = StockSymbol.ALL.map { it.ticker }
        assertEquals(
            "Duplicate tickers detected: ${tickers.groupBy { it }.filter { it.value.size > 1 }.keys}",
            tickers.size,
            tickers.toSet().size
        )
    }

    @Test
    fun `find returns the correct symbol for a known ticker`() {
        val result = StockSymbol.find("AAPL")
        assertNotNull(result)
        assertEquals("Apple Inc.", result!!.name)
    }

    @Test
    fun `find returns null for a ticker that is not in the list`() {
        assertNull(StockSymbol.find("FAKE"))
    }

    @Test
    fun `every symbol has a non-blank name`() {
        StockSymbol.ALL.forEach { stock ->
            assertTrue("${stock.ticker} has a blank name", stock.name.isNotBlank())
        }
    }

    @Test
    fun `every symbol has a non-blank description`() {
        StockSymbol.ALL.forEach { stock ->
            assertTrue("${stock.ticker} has a blank description", stock.description.isNotBlank())
        }
    }

    // ─── toPriceRecord – direction logic ─────────────────────────────────────

    private fun update(symbol: String = "AAPL", price: Double = 150.0) =
        PriceUpdate(symbol = symbol, price = price, timestamp = 1_000L)

    @Test
    fun `direction is UNCHANGED when there is no previous record`() {
        val record = update().toPriceRecord(previous = null)
        assertEquals(PriceDirection.UNCHANGED, record.direction)
    }

    @Test
    fun `direction is UP when the new price is higher than the previous price`() {
        val prev = update(price = 100.0).toPriceRecord(null)
        val record = update(price = 110.0).toPriceRecord(prev)
        assertEquals(PriceDirection.UP, record.direction)
    }

    @Test
    fun `direction is DOWN when the new price is lower than the previous price`() {
        val prev = update(price = 200.0).toPriceRecord(null)
        val record = update(price = 180.0).toPriceRecord(prev)
        assertEquals(PriceDirection.DOWN, record.direction)
    }

    @Test
    fun `direction is UNCHANGED when the price does not move`() {
        val prev = update(price = 150.0).toPriceRecord(null)
        val record = update(price = 150.0).toPriceRecord(prev)
        assertEquals(PriceDirection.UNCHANGED, record.direction)
    }

    // ─── toPriceRecord – flashTrigger ────────────────────────────────────────

    @Test
    fun `flashTrigger starts at 1 when there is no previous record`() {
        val record = update().toPriceRecord(previous = null)
        assertEquals(1, record.flashTrigger)
    }

    @Test
    fun `flashTrigger increments by 1 on each successive update`() {
        val first  = update(price = 100.0).toPriceRecord(null)
        val second = update(price = 110.0).toPriceRecord(first)
        val third  = update(price = 120.0).toPriceRecord(second)

        assertEquals(2, second.flashTrigger)
        assertEquals(3, third.flashTrigger)
    }

    // ─── toPriceRecord – prevPrice ────────────────────────────────────────────

    @Test
    fun `prevPrice equals the current price when there is no previous record`() {
        val record = update(price = 150.0).toPriceRecord(null)
        assertEquals(150.0, record.prevPrice, 0.001)
    }

    @Test
    fun `prevPrice captures the price from the preceding record`() {
        val prev   = update(price = 100.0).toPriceRecord(null)
        val record = update(price = 120.0).toPriceRecord(prev)

        assertEquals(100.0, record.prevPrice, 0.001)
        assertEquals(120.0, record.price,     0.001)
    }

    // ─── toPriceRecord – metadata lookup ─────────────────────────────────────

    @Test
    fun `name is resolved from the StockSymbol catalogue`() {
        val record = update(symbol = "AAPL").toPriceRecord(null)
        assertEquals("Apple Inc.", record.name)
    }

    @Test
    fun `name is empty string when the ticker is not in the catalogue`() {
        val record = update(symbol = "UNKNOWN").toPriceRecord(null)
        assertEquals("", record.name)
    }

    @Test
    fun `description is resolved from the StockSymbol catalogue`() {
        val record = update(symbol = "MSFT").toPriceRecord(null)
        assertTrue(record.description.contains("Microsoft"))
    }

    @Test
    fun `symbol and price fields are preserved on the resulting record`() {
        val record = update(symbol = "NVDA", price = 500.0).toPriceRecord(null)
        assertEquals("NVDA",  record.symbol)
        assertEquals(500.0,   record.price, 0.001)
    }
}
