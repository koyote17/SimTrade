package com.example.simtrade.data.remote

import com.example.simtrade.data.model.CryptoCurrency
import com.example.simtrade.data.model.ExchangeRateResponse
import com.example.simtrade.data.model.MarketChartResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface CoinApiService {

    @GET("coins/markets")

fun getCoinMarkets(
    @Query("vs_currency") vsCurrency: String,
    @Query("order") order: String = "market_cap_desc",
    @Query("per_page") perPage: Int = 300,
    @Query("sparkline") sparkline: Boolean = false,
    @Query("price_change_percentage") priceChangePercentage: String = "24h"
): Single<List<CryptoCurrency>>

    @GET("coins/{id}/market_chart")
    fun getMarketChartLast24h(
        @Path("id") coinId: String,
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("days") days: Int = 1
    ): Single<MarketChartResponse>

    @GET("simple/price")
    fun getExchangeRates(
        @Query("ids") ids: String = "usd",
        @Query("vs_currencies") vsCurrencies: String = "pln,eur,gbp,uah"
    ): Single<ExchangeRateResponse>

}