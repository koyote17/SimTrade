package com.example.simtrade.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simtrade.data.local.entities.FavCryptoEntity
import com.example.simtrade.data.local.entities.UserEntity
import com.example.simtrade.data.local.entities.WalletEntity
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

enum class SortCriteria {
    NONE,
    PRICE_ASC, PRICE_DESC,
    NAME_ASC,
    NAME_DESC,
    CHANGE_24H_ASC,
    CHANGE_24H_DESC
}

class CryptoViewModel(private val repository: CryptoRepository) : ViewModel() {

    private val _selectedCurrency = MutableStateFlow("usd")
    val selectedCurrency = _selectedCurrency.asStateFlow()

    private val _allCryptos = MutableStateFlow<Result<List<CryptoCurrency>>>(Result.Loading)
    val allCryptos = _allCryptos.asStateFlow()

    val top10Coins: StateFlow<Result<List<CryptoCurrency>>> = _allCryptos.map { result ->
        when(result) {
            is Result.Success -> {
                Result.Success(result.data.sortedByDescending { it.marketCap ?: 0.0}.take(10))
            }
            else -> result
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Result.Loading
    )

    private var cachedRates: FiatRates? = null
    private val _favCryptoIds = MutableStateFlow<Set<String>>(emptySet())
    val favCryptoIds: StateFlow<Set<String>> = _favCryptoIds.asStateFlow()

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    private val _walletItems = MutableStateFlow<List<WalletEntity>>(emptyList())
    val walletItems: StateFlow<List<WalletEntity>> = _walletItems.asStateFlow()

    private val _showAddFundsDialog = MutableStateFlow(false)
    val showAddFundsDialog: StateFlow<Boolean> = _showAddFundsDialog.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _sortCriteria = MutableStateFlow(SortCriteria.NONE)
    val sortCriteria: StateFlow<SortCriteria> = _sortCriteria.asStateFlow()

    val filteredAndSortedCryptos: StateFlow<Result<List<CryptoCurrency>>> =
        _allCryptos.combine(_searchText) { cryptos, text ->
            if (cryptos is Result.Success) {
                val filteredList = if (text.isBlank()) {
                    cryptos.data
                } else {
                    cryptos.data.filter { coin ->
                        coin.name.contains(text, ignoreCase = true) ||
                                coin.symbol.contains(text, ignoreCase = true)
                    }
                }
                Result.Success(filteredList)
            } else {
                cryptos
            }
        }.combine(_sortCriteria) { filteredResult, criteria ->
            if (filteredResult is Result.Success) {
                val sortedList = when (criteria) {
                    SortCriteria.NAME_ASC -> filteredResult.data.sortedBy { it.name }
                    SortCriteria.NAME_DESC -> filteredResult.data.sortedByDescending { it.name }
                    SortCriteria.PRICE_ASC -> filteredResult.data.sortedBy { it.currentPrice }
                    SortCriteria.PRICE_DESC -> filteredResult.data.sortedByDescending { it.currentPrice }
                    SortCriteria.CHANGE_24H_ASC -> filteredResult.data.sortedBy { it.priceChangePercentage }
                    SortCriteria.CHANGE_24H_DESC -> filteredResult.data.sortedByDescending { it.priceChangePercentage }
                    else -> filteredResult.data
                }
                Result.Success(sortedList)
            } else {
                filteredResult
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)


    val totalPortfolioValue: StateFlow<Double> = combine(
        _user,
        _walletItems,
        _allCryptos.map { if (it is Result.Success) it.data else null },
        _selectedCurrency
    ) { user, wallet, cryptos, currency ->
        if (user == null || cryptos == null) return@combine 0.0
        val cryptoValueInUsd = wallet.sumOf { walletItem ->
            val crypto = cryptos.find { it.symbol == walletItem.cryptoSymbol }
            walletItem.quantity * (crypto?.currentPrice ?: 0.0)
        }

        val totalUsdValue = user.fiatBalance + cryptoValueInUsd

        val rate = getRateForCurrency(currency, cachedRates ?: FiatRates())
        totalUsdValue * rate
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0.0
    )

    init {
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
            .onEach { result -> _allCryptos.value = result }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            try {
                val ratesResponse = repository.getExchangeRates().await()
                cachedRates = ratesResponse.usd
            } catch (e: Exception) {
                // Obsługa błędu
            }
        }

        fetchUser(1)

        repository.getFavCrypto()
            .onEach { favCryptoEntities ->
                _favCryptoIds.value = favCryptoEntities.map { it.cryptoId }.toSet()
            }
            .launchIn(viewModelScope)
    }

    fun toggleFavCrypto(crypto: CryptoCurrency){
        viewModelScope.launch {
            val isCurrentlyFav = repository.isCryptoFav(crypto.id)

            if (isCurrentlyFav) {
                repository.deleteFavCrypto(crypto.id)
            }
            else {
                val newFav = FavCryptoEntity(
                    cryptoId = crypto.id,
                    symbol = crypto.symbol,
                    name = crypto.name
                )
                repository.insertFavCrypto(newFav)
            }
        }
    }

    fun fetchUser(userId: Long) {
        viewModelScope.launch {
            val userFromDb = repository.getUser(userId)
            if (userFromDb == null) {
                val newUser = UserEntity(fiatBalance = 1000.0)
                repository.insertUser(newUser)
                _user.value = newUser
            } else {
                _user.value = userFromDb
            }
        }
        viewModelScope.launch {
            repository.getWalletItems(userId).collect { walletItemsList ->
                _walletItems.value = walletItemsList
            }
        }
    }

    fun addFunds(userId: Long, amount: Double) {
        viewModelScope.launch {
            val currentUser = repository.getUser(userId)
            if (currentUser != null) {
                val updatedUser = currentUser.copy(fiatBalance = currentUser.fiatBalance + amount)
                repository.updateUser(updatedUser)
            }
        }
    }

    fun showAddFundsDialog() {
        _showAddFundsDialog.value = true
    }

    fun dismissAddFundsDialog() {
        _showAddFundsDialog.value = false
    }

    fun setCurrency(currency: String) {
        _selectedCurrency.value = currency.lowercase()
    }

    fun setSearchText(text: String) {
        _searchText.value = text
    }

    fun setSortCriteria(criteria: SortCriteria) {
        _sortCriteria.value = criteria
    }

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