package com.example.simtrade.data.model


data class ExchangeRateResponse(
    val usd: FiatRates
)

data class FiatRates(
    val pln: Double = 1.0,
    val eur: Double = 1.0,
    val gbp: Double = 1.0,
    val uah: Double = 1.0
)