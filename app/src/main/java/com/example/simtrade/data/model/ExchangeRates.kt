package com.example.simtrade.data.model


data class ExchangeRateResponse(
    val usd: FiatRates
)

data class FiatRates(
    val pln: Double,
    val eur: Double,
    val gbp: Double,
    val uah: Double
)