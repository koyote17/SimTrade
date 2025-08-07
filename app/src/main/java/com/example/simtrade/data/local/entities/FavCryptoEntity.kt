package com.example.simtrade.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cryptos")
data class FavCryptoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cryptoId: String = "",
    val name: String = "",
    val symbol: String = ""

)
