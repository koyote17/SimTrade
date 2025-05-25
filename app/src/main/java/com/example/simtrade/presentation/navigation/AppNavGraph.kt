package com.example.simtrade.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simtrade.data.remote.RetrofitInstance
import com.example.simtrade.data.repository.CryptoRepository
import com.example.simtrade.presentation.screens.HomeScreen
import com.example.simtrade.presentation.screens.LoginScreen
import com.example.simtrade.presentation.viewmodel.AuthViewModel
import com.example.simtrade.presentation.viewmodel.CryptoViewModel

@Composable
fun AppNavGraph(){
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel() }
    val api = remember { RetrofitInstance.retrofit }
    val cryptoRepository = remember { CryptoRepository(api) }
    val cryptoViewModel = remember { CryptoViewModel(cryptoRepository) }


    NavHost(navController = navController, startDestination = "login"){
        composable("login") {
            LoginScreen(viewModel = authViewModel,
                        onLoginSuccess = { navController.navigate("home"){
                            popUpTo("login") { inclusive = true }
                        } }
            )
        }
        composable("home"){
            HomeScreen(viewModel = cryptoViewModel,
                onNavigateTo = { navController.navigate(it)
                })
        }
    }
}