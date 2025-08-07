package com.example.simtrade.presentation.navigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simtrade.data.remote.RetrofitInstance
import com.example.simtrade.data.repository.CryptoRepository
import com.example.simtrade.presentation.components.BottomNavItems
import com.example.simtrade.presentation.screens.AllCryptosScreen
import com.example.simtrade.presentation.screens.BuySellScreen
import com.example.simtrade.presentation.screens.HistoryScreen
import com.example.simtrade.presentation.screens.HomeScreen
import com.example.simtrade.presentation.screens.LoginScreen
import com.example.simtrade.presentation.viewmodel.AuthViewModel
import com.example.simtrade.presentation.viewmodel.CryptoViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.SimTradeApp
import com.example.simtrade.presentation.screens.WalletDetailsScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel() }

    val context = LocalContext.current.applicationContext
    val app = remember(context) { context as SimTradeApp }

    // Używamy już zdefiniowanego w SimTradeApp repozytorium
    val cryptoRepository = remember { app.cryptoRepository }
    val cryptoViewModel = remember { CryptoViewModel(cryptoRepository) }

    val isLoggedIn = remember { derivedStateOf { authViewModel.isLoggedIn } }
    var selectedItem by remember { mutableStateOf("home") }

    if (!isLoggedIn.value) {
        LoginScreen(viewModel = authViewModel) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    BottomNavItems.bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.item, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = selectedItem == item.route,
                            onClick = {
                                selectedItem = item.route
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        )
        { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        viewModel = cryptoViewModel,
                        onNavigateToDetailsScreen = { navController.navigate("wallet_details") }
                    )
                }
                composable("wallet_details") {
                    WalletDetailsScreen(
                        viewModel = cryptoViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("all_cryptos") {
                    AllCryptosScreen(
                        viewModel = cryptoViewModel,
                        onNavigateTo = { navController.navigate(it) }
                    )
                }
                composable("buy_sell") { BuySellScreen() }
                composable("history") { HistoryScreen() }
            }
        }
    }
}
