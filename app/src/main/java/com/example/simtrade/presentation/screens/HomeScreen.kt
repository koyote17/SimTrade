package com.example.simtrade.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simtrade.presentation.components.CurrencySelector
import com.example.simtrade.presentation.components.BalanceCard
import com.example.simtrade.presentation.viewmodel.CryptoViewModel
import com.example.simtrade.presentation.viewmodel.Result

@Composable
fun HomeScreen(
    viewModel: CryptoViewModel,
    onNavigateToDetailsScreen: () -> Unit
) {
    val cryptoResult by viewModel.top10Coins.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val balance by viewModel.totalPortfolioValue.collectAsState()

    val supportedCurrencies = listOf("usd", "eur", "gbp", "pln", "uah")

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchUser(1)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            CurrencySelector(
                selectedCurrency = selectedCurrency,
                onCurrencySelected = { currency -> viewModel.setCurrency(currency) },
                supportedCurrencies = supportedCurrencies
            )
        }

        BalanceCard(
            balance = balance,
            selectedCurrency = selectedCurrency,
            onCardClick = onNavigateToDetailsScreen
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            text = "Top 10 Crypto",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        when (val result = cryptoResult) {
            is Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier
                    .padding(top = 20.dp),
                    trackColor = Color.Green)
            }

            is Result.Success -> {
                val top10 = result.data.sortedByDescending { it.currentPrice }.take(10)
                LazyColumn(modifier = Modifier.weight(1F)) {
                    items(top10) { coin ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                .clickable { },
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(coin.name, fontWeight = FontWeight.Bold)
                                    Text(coin.symbol, color = Color.Gray)
                                }
                                Text(
                                    text = "${
                                        String.format("%.2f", coin.currentPrice)
                                    } ${selectedCurrency.uppercase()}",
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF388E3C)
                                )
                            }
                        }
                    }
                }
            }

            is Result.Error -> {
                Text(
                    text = "Error while loading data: ${result.message}",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

        Spacer(Modifier.padding(16.dp))

    }
}
