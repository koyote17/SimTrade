package com.example.simtrade.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simtrade.data.local.dao.FavCryptoDao
import com.example.simtrade.data.local.dao.UserDao
import com.example.simtrade.data.local.dao.WalletDao
import com.example.simtrade.data.local.entities.FavCryptoEntity
import com.example.simtrade.data.local.entities.UserEntity
import com.example.simtrade.data.local.entities.WalletEntity

@Database(version = 2, entities = [FavCryptoEntity::class, UserEntity::class, WalletEntity::class], exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun favCryptoDao(): FavCryptoDao
    abstract fun userDao(): UserDao
    abstract fun walletDao(): WalletDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "crypto_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}