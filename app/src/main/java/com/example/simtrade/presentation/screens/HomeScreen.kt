package com.example.simtrade.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simtrade.presentation.viewmodel.CryptoViewModel


@Composable
fun HomeScreen(viewModel: CryptoViewModel,
               onNavigateTo: (String) -> Unit) {

    LaunchedEffect(Unit) {
        viewModel.fetchCryptos()
    }

    val cryptoList by viewModel.cryptos.collectAsState()
    val top10 = cryptoList.sortedByDescending { it.currentPrice }.take(10)
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()

   // var expanded by remember { mutableStateOf(false) }
   // val currencies = listOf("usd", "eur", "gbp", "pln")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp))
     {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Saldo", color = Color.White, fontSize = 16.sp)
                Text("1000 USD", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
         Spacer(modifier = Modifier.height(16.dp))

         Text(modifier = Modifier.padding(16.dp),
             textAlign = TextAlign.Center,
             text = "Top 10 Kryptowalut",
             fontSize = 20.sp,
             fontWeight = FontWeight.SemiBold)

         LazyColumn(modifier = Modifier.weight(1F)) {
             items(top10) { coin ->
                 Card(modifier = Modifier
                     .fillMaxWidth()
                     .padding(vertical = 6.dp)
                     .clickable { /* details or action*/ },
                     elevation = CardDefaults.cardElevation(6.dp)
                 ) {
                     Row(modifier = Modifier
                         .padding(16.dp)
                         .fillMaxWidth(),
                         verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                     ){
                         Column {
                             Text(coin.name, fontWeight = FontWeight.Bold)
                             Text(coin.symbol, color = Color.Gray)
                         }
                         Text(text = "$${String.format("%.2f", coin.currentPrice)}",
                             fontWeight = FontWeight.SemiBold,
                             color = Color(0xFF388E3C)
                         )
                     }
                 }
             }
         }

         Spacer(Modifier.padding(16.dp))

         Row(modifier = Modifier
             .fillMaxWidth(),
             horizontalArrangement = Arrangement.SpaceEvenly)
         {
             Button(onClick = {onNavigateTo("buy_sell")}){
                 Text("Kup/Sprzedaj")
             }
             Button(onClick = { onNavigateTo("all_cryptos")}) {
                 Text("Wszystkie kursy")
             }
         }
    }

}