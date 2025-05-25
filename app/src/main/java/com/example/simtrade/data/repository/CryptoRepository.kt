package com.example.simtrade.data.repository

import com.example.simtrade.data.model.CryptoCurrency
import com.example.simtrade.data.remote.CoinApiService
import io.reactivex.rxjava3.core.Single

class CryptoRepository(private val api: CoinApiService) {

    fun getCryptos(currency: String): Single<List<CryptoCurrency>>{
        return api.getTopCoins(vsCurrency = currency)
    }
}