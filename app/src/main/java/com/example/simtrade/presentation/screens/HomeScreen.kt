package com.example.simtrade.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simtrade.presentation.viewmodel.CryptoViewModel
import com.example.simtrade.presentation.viewmodel.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CryptoViewModel,
    onNavigateTo: (String) -> Unit
) {
    val cryptoResult by viewModel.cryptos.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val balance by viewModel.convertedBalance.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    val supportedCurrencies = listOf("usd", "eur", "gbp", "pln", "uah")

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
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedCurrency.uppercase(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Currency") },
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown"
                        )
                    },
                    modifier = Modifier.menuAnchor().width(IntrinsicSize.Min),
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    supportedCurrencies.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text(currency.uppercase()) },
                            onClick = {
                                expanded = false
                                viewModel.setCurrency(currency)
                            }
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Balance: ${String.format("%.2f", balance)} ${selectedCurrency.uppercase()}",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            text = "Top 10 Crypto",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        when (val result = cryptoResult) {
            is Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 20.dp))
            }
            is Result.Success -> {
                val top10 = result.data.sortedByDescending { it.currentPrice }.take(10)
                LazyColumn(modifier = Modifier.weight(1F)) {
                    items(top10) { coin ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { },
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
                                    text = "${String.format("%.2f", coin.currentPrice)} ${selectedCurrency.uppercase()}",
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