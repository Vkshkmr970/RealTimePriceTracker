package com.power.realtimepricetracker.data.model

enum class PriceDirection { UP, DOWN, UNCHANGED }

data class PriceRecord(
    val symbol: String,
    val name: String,
    val description: String,
    val price: Double,
    val prevPrice: Double,
    val direction: PriceDirection,
    val flashTrigger: Int = 0
)
