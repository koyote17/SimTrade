package com.example.simtrade.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simtrade.R
import com.example.simtrade.presentation.components.BalanceCard
import com.example.simtrade.presentation.components.CurrencySelector

import com.example.simtrade.presentation.viewmodel.CryptoViewModel
import com.example.simtrade.presentation.viewmodel.Result

@Composable
fun AllCryptosScreen(viewModel: CryptoViewModel,
                     onNavigateToDetailScreen: () -> Unit) {
    val allCryptoResult by viewModel.allCryptos.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val supportedCurrencies = listOf("usd", "eur", "gbp", "pln", "uah")
    val balance by viewModel.totalPortfolioValue.collectAsState()

    var currencySelectorExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            BalanceCard(
                balance = balance,
                selectedCurrency = selectedCurrency,
                onCardClick = onNavigateToDetailScreen,
                modifier = Modifier.weight(1F)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box {
                IconButton(
                    onClick = { currencySelectorExpanded = true }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_currency_exchange),
                        contentDescription = "Select currency",
                        modifier = Modifier.size(24.dp)
                    )
                }
                DropdownMenu(
                    expanded = currencySelectorExpanded,
                    onDismissRequest = { currencySelectorExpanded = false },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    supportedCurrencies.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text(currency.uppercase()) },
                            onClick = {
                                viewModel.setCurrency(currency)
                                currencySelectorExpanded = false
                            }
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text("Search by name or symbol") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Pasek sortowania pozostaje w osobnym wierszu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sort by: ", fontWeight = FontWeight.Bold)
            IconButton(onClick = { }) {
                Icon(Icons.Default.Sort, contentDescription = "Sort")
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (val result = allCryptoResult) {
                is Result.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 20.dp),
                        trackColor = Color.Green
                    )
                }
                is Result.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(result.data) { coin ->
                            // Zmiana: implementacja elementu listy bezpośrednio tutaj, zgodnie z pierwotnym stylem
                            val favCryptoIds by viewModel.favCryptoIds.collectAsState()
                            val isCoinFavorite = favCryptoIds.contains(coin.id)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { /* onItemClick(coin.id) */ },
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Column(modifier = Modifier.weight(1F)) {
                                        Text(coin.name, fontWeight = FontWeight.Bold)
                                        Text(coin.symbol.uppercase(), color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${String.format("%.2f", coin.currentPrice)} ${selectedCurrency.uppercase()}",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp
                                        )
                                        coin.priceChangePercentage?.let { change ->
                                            val changeColor = if (change >= 0) Color(0xFF388E3C) else Color.Red
                                            val changeIcon = if (change >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = changeIcon,
                                                    contentDescription = "Change",
                                                    tint = changeColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = "${String.format("%.2f", change)}%",
                                                    color = changeColor,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                    IconButton(onClick = { viewModel.toggleFavCrypto(coin) }) {
                                        Icon(
                                            imageVector = if (isCoinFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = if (isCoinFavorite) "Remove from favorites" else "Add to favorites",
                                            tint = if (isCoinFavorite) Color.Red else LocalContentColor.current
                                        )
                                    }
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
        }
    }
}