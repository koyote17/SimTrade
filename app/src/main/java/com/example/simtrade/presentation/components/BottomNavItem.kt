package com.example.simtrade.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.simtrade.data.model.BottomNavItem


object BottomNavItems{
   val bottomNavItems = listOf(
       BottomNavItem("Home", "home", Icons.Filled.Home),
       BottomNavItem("Wszystkie", "all_cryptos", Icons.Filled.List),
       BottomNavItem("Kup/Sprzedaj", "buy_sell", Icons.Filled.ShoppingCart),
       BottomNavItem("Historia", "history", Icons.Rounded.DateRange))
}