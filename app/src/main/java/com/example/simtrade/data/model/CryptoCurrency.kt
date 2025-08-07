package com.example.simtrade.data.model

import com.google.gson.annotations.SerializedName
import java.net.URL

data class CryptoCurrency(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    @SerializedName("current_price")
    val currentPrice: Double,
    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage: Double,
    @SerializedName("market_cap")
    val marketCap: Double?,
    @SerializedName("total_volume")
    val totalVolume: Double?)