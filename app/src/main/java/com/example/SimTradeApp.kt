package com.example

import android.app.Application
import com.example.simtrade.data.local.AppDatabase
import com.example.simtrade.data.local.dao.FavCryptoDao
import com.example.simtrade.data.local.dao.UserDao
import com.example.simtrade.data.local.dao.WalletDao
import com.example.simtrade.data.remote.CoinApiService
import com.example.simtrade.data.remote.RetrofitInstance
import com.example.simtrade.data.repository.CryptoRepository
import com.google.firebase.FirebaseApp

class SimTradeApp: Application() {
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val favCryptoDao: FavCryptoDao by lazy {
        database.favCryptoDao()
    }

    val userDao: UserDao by lazy {
        database.userDao()
    }

    val walletDao: WalletDao by lazy {
        database.walletDao()
    }

    private val coinApiService: CoinApiService by lazy {
        RetrofitInstance.retrofit
    }

    val cryptoRepository: CryptoRepository by lazy {
        CryptoRepository(
            api = coinApiService,
            favCryptoDao = favCryptoDao,
            userDao = userDao,
            walletDao = walletDao
        )
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}