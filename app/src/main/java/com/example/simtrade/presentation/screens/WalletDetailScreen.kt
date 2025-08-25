package com.example.simtrade.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.simtrade.presentation.components.BalanceCard
import com.example.simtrade.presentation.viewmodel.CryptoViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailsScreen(
    viewModel: CryptoViewModel,
    onNavigateBack: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val walletItems by viewModel.walletItems.collectAsState()
    val totalValue by viewModel.totalPortfolioValue.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val showDialog by viewModel.showAddFundsDialog.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły portfela") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wstecz")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                BalanceCard(
                    balance = totalValue,
                    selectedCurrency = selectedCurrency,
                    onCardClick = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Sekcja z walutami fiat
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Fiat Funds",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = String.format("Dostępne USD: %.2f", user?.fiatBalance ?: 0.0),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.showAddFundsDialog() },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Dodaj środki", modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Dodaj środki")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Sekcja z posiadany mi kryptowalutami w Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Cryptocurrency Holdings",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        walletItems.forEach { walletItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = walletItem.cryptoSymbol,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = String.format("%.4f", walletItem.quantity),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Divider()
                        }
                    }
                }
            }

            items(walletItems) { walletItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = walletItem.cryptoSymbol,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = String.format("%.4f", walletItem.quantity),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    // Okienko dialogowe
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissAddFundsDialog() },
            title = {
                Text("Warning: Adding funds")
            },
            text = {
                Text("Adding funds to your wallet will increase the commission fee for all future transactions. The commission will increase by X%.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addFunds(user?.id ?: 1, 100.0)
                        viewModel.dismissAddFundsDialog()
                    }
                ) {
                    Text("I understand")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissAddFundsDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}