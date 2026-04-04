package com.power.realtimepricetracker.data.model
import kotlinx.serialization.Serializable

@Serializable
data class PriceUpdate(
    val symbol: String,
    val price: Double,
    val timestamp: Long
)

// Extension function to convert PriceUpdate to PriceRecord
fun PriceUpdate.toPriceRecord(previous: PriceRecord?): PriceRecord {
    val stock = StockSymbol.find(symbol)
    val direction = when {
        previous == null        -> PriceDirection.UNCHANGED
        price > previous.price  -> PriceDirection.UP
        price < previous.price  -> PriceDirection.DOWN
        else                    -> PriceDirection.UNCHANGED
    }
    return PriceRecord(
        symbol      = symbol,
        name        = stock?.name.orEmpty(),
        description = stock?.description.orEmpty(),
        price       = price,
        prevPrice   = previous?.price ?: price,
        direction   = direction,
        flashTrigger = (previous?.flashTrigger ?: 0) + 1
    )
}
