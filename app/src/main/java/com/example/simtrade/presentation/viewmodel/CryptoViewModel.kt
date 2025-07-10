package com.example.simtrade.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simtrade.data.model.CryptoCurrency
import com.example.simtrade.data.model.FiatRates
import com.example.simtrade.data.repository.CryptoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.await


sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}


class CryptoViewModel(private val repository: CryptoRepository) : ViewModel() {

    private val _selectedCurrency = MutableStateFlow("usd")
    val selectedCurrency = _selectedCurrency.asStateFlow()

    private val baseBalanceUsd = 1000.0
    private val _convertedBalance = MutableStateFlow(baseBalanceUsd)
    val convertedBalance = _convertedBalance.asStateFlow()

    private val _cryptos = MutableStateFlow<Result<List<CryptoCurrency>>>(Result.Loading)
    val cryptos = _cryptos.asStateFlow()

    private var cachedRates: FiatRates? = null

    init {
        // Strumień pobierający kryptowaluty, gdy zmienia się waluta
        _selectedCurrency
            .flatMapLatest { currency ->
                flow {
                    emit(Result.Loading)
                    try {
                        val cryptoList = repository.getCryptos(currency).await()
                        emit(Result.Success(cryptoList))
                    } catch (e: Exception) {
                        emit(Result.Error(e.message ?: "An unknown error has occurred."))
                    }
                }
            }
            .onEach { result -> _cryptos.value = result }
            .launchIn(viewModelScope)

        _selectedCurrency
            .onEach { currency ->
                updateBalanceConversion(currency)
            }
            .launchIn(viewModelScope)
    }

    fun setCurrency(currency: String) {
        _selectedCurrency.value = currency.lowercase()
    }

    private fun updateBalanceConversion(currency: String) {
        if (currency == "usd") {
            _convertedBalance.value = baseBalanceUsd
            return
        }

        cachedRates?.let { rates ->
            val rate = getRateForCurrency(currency, rates)
            _convertedBalance.value = baseBalanceUsd * rate
            return
        }

        viewModelScope.launch {
            try {
                val ratesResponse = repository.getExchangeRates().await()
                cachedRates = ratesResponse.usd
                val rate = getRateForCurrency(currency, ratesResponse.usd)
                _convertedBalance.value = baseBalanceUsd * rate
            } catch (e: Exception) {
                _convertedBalance.value = baseBalanceUsd
            }
        }
    }


     // Pomocnicza funkcja do wyciągania odpowiedniego kursu z obiektu.

    private fun getRateForCurrency(currency: String, rates: FiatRates): Double {
        return when (currency) {
            "pln" -> rates.pln
            "eur" -> rates.eur
            "gbp" -> rates.gbp
            "uah" -> rates.uah
            else -> 1.0
        }
    }
}