package com.example.simtrade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.simtrade.presentation.navigation.AppNavGraph
import com.example.simtrade.ui.theme.SimTradeTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimTradeTheme {
                AppNavGraph()
            }
        }
    }
}
