package com.example.simtrade.data.remote

import com.example.simtrade.data.model.CryptoCurrency
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface CoinApiService {

    @GET("coins/markets")

fun getTopCoins(
    @Query("vs_currency") vsCurrency: String,
    @Query("order") order: String = "market_cap_desc",
    @Query("per_page") perPage: Int = 30,
    @Query("sparkline") sparkline: Boolean = false
): Single<List<CryptoCurrency>>
}