package com.example.simtrade.data.model

data class MarketChartResponse(
    val prices: List<List<Double>>,
    val market_caps: List<List<Double>>,
    val total_volumes: List<List<Double>>
)
