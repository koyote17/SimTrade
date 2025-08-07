package com.example.simtrade.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.simtrade.data.local.entities.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletItem(wallet: WalletEntity)

    @Query("SELECT * FROM wallet WHERE userId = :userId")
    fun getWalletItemsForUser(userId: Long): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallet WHERE userId = :userId AND cryptoSymbol = :symbol")
    suspend fun getWalletItem(userId: Long, symbol: String): WalletEntity?

    @Delete
    suspend fun deleteWalletItem(wallet: WalletEntity)

    @Update
    suspend fun updateWalletItem(wallet: WalletEntity)


}