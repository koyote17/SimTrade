package com.example.simtrade.presentation.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simtrade.data.model.CryptoCurrency
import com.example.simtrade.presentation.components.BalanceCard
import com.example.simtrade.presentation.components.CurrencySelector
import com.example.simtrade.presentation.viewmodel.CryptoViewModel
import com.example.simtrade.presentation.viewmodel.Result


@Composable
fun AllCryptosScreen(viewModel: CryptoViewModel,
                     onNavigateTo: (String) -> Unit) {
    val allCryptoResult by viewModel.allCryptos.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val balance by viewModel.totalPortfolioValue.collectAsState()

    val supportedCurrencies = listOf("usd", "eur", "gbp", "pln", "uah")

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ){
            CurrencySelector(
                selectedCurrency = selectedCurrency,
                onCurrencySelected = { currency -> viewModel.setCurrency(currency) },
                supportedCurrencies = supportedCurrencies)
        }

        BalanceCard(balance = balance,
            selectedCurrency = selectedCurrency,
            onCardClick = { /*Todo*/ })

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = " ",
            onValueChange = { },
            label = { Text("Search by name or symbol") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true)

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically)
        {
          Text("Sort by: ", fontWeight = FontWeight.Bold)
            IconButton(onClick = { } ) {
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
                    CircularProgressIndicator(modifier = Modifier
                        .padding(top = 20.dp),
                        trackColor = Color.Green)
                }
                is Result.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(result.data) { coin ->
                            val favCryptoIds by viewModel.favCryptoIds.collectAsState()
                            val isCoinFavorite = favCryptoIds.contains(coin.id)
                            CryptoListItem(
                                coin = coin,
                                selectedCurrency = selectedCurrency,
                                onFavoriteClick = { clickedCoin -> viewModel.toggleFavCrypto(clickedCoin) },
                                isFavorite = isCoinFavorite
                            )
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

        Spacer(Modifier.padding(16.dp))
    }
}


@Composable
fun CryptoListItem(
    coin: CryptoCurrency,
    selectedCurrency: String,
    onFavoriteClick: (CryptoCurrency) -> Unit,
    isFavorite: Boolean

) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { /* onItemClick(coin.id) */ }, // Klikalny element
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
                Text(coin.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis)
                Text(coin.symbol.uppercase(),
                    color = Color.Gray,
                    fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))


            // Cena i zmiana procentowa 24h
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%.2f", coin.currentPrice)} ${selectedCurrency.uppercase()}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                // Wyświetlamy procentową zmianę z kolorem i strzałką
                coin.priceChangePercentage?.let { change -> // Upewniamy się, że wartość nie jest null
                    val changeColor = if (change >= 0) Color(0xFF388E3C) else Color.Red // Zieleń dla wzrostu, czerwień dla spadku
                    val changeIcon = if (change >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = changeIcon,
                            contentDescription = "Change",
                            tint = changeColor,
                            modifier = Modifier.size(16.dp))
                        Text(
                            text = "${String.format("%.2f", change)}%",
                            color = changeColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            //Implementation in future
            IconButton(onClick = { onFavoriteClick(coin) }) {
                Icon(imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Red else LocalContentColor.current)
            }
        }
    }
}
