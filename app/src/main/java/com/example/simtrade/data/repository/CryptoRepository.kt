package com.example.simtrade.data.repository

import com.example.simtrade.data.local.dao.FavCryptoDao
import com.example.simtrade.data.local.dao.UserDao
import com.example.simtrade.data.local.dao.WalletDao
import com.example.simtrade.data.local.entities.FavCryptoEntity
import com.example.simtrade.data.local.entities.UserEntity
import com.example.simtrade.data.local.entities.WalletEntity
import com.example.simtrade.data.model.CryptoCurrency
import com.example.simtrade.data.remote.CoinApiService
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

class CryptoRepository(
    private val api: CoinApiService,
    private val favCryptoDao: FavCryptoDao,
    private val userDao: UserDao,
    private val walletDao: WalletDao

) {

    fun getCryptos(vsCurrency: String): Single<List<CryptoCurrency>>{
        return api.getCoinMarkets(vsCurrency = vsCurrency)
    }

    fun getMarketChartLast24h(coinId: String, vsCurrency: String = "usd") =
        api.getMarketChartLast24h(coinId, vsCurrency)

    fun getExchangeRates() = api.getExchangeRates()

    suspend fun insertFavCrypto(crypto: FavCryptoEntity){
        favCryptoDao.insertFavCrypto(crypto)
    }

    suspend fun deleteFavCrypto(cryptoId: String){
        favCryptoDao.deleteFavCrypto(cryptoId)
    }
    fun getFavCrypto(): Flow<List<FavCryptoEntity>> {
        return favCryptoDao.getAllFavCryptos()
    }
    suspend fun isCryptoFav(cryptoId: String): Boolean {
        return favCryptoDao.isCryptoFav(cryptoId)
    }

    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    suspend fun getUser(userId: Long) = userDao.getUser(userId)
    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    fun getWalletItems(userId: Long): Flow<List<WalletEntity>> = walletDao.getWalletItemsForUser(userId)
    suspend fun getWalletItem(userId: Long, symbol: String) = walletDao.getWalletItem(userId, symbol)
    suspend fun updateWalletItem(wallet: WalletEntity) = walletDao.updateWalletItem(wallet)
    suspend fun insertWalletItem(wallet: WalletEntity) = walletDao.insertWalletItem(wallet)



}

