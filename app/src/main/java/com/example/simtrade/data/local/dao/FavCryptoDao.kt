package com.example.simtrade.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.simtrade.data.local.entities.FavCryptoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavCryptoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavCrypto(crypto: FavCryptoEntity)

    @Query("DELETE FROM favorite_cryptos WHERE cryptoId = :cryptoId")
    suspend fun deleteFavCrypto(cryptoId: String)

    @Query("SELECT * FROM favorite_cryptos")
    fun getAllFavCryptos(): Flow<List<FavCryptoEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cryptos WHERE cryptoId = :cryptoId LIMIT 1)")
    suspend fun isCryptoFav(cryptoId: String) : Boolean
}