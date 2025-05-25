package com.example.simtrade.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.simtrade.data.model.CryptoCurrency
import com.example.simtrade.data.repository.CryptoRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow


class CryptoViewModel(private val repository: CryptoRepository): ViewModel() {

    private val _cryptos = MutableStateFlow<List<CryptoCurrency>>(emptyList())
    val cryptos: StateFlow<List<CryptoCurrency>> = _cryptos

    private val _selectedCurrency = MutableStateFlow("usd")
    val selectedCurrency: StateFlow<String> = _selectedCurrency

    private val disposable = CompositeDisposable()

    init {
        fun fetchCryptos(){
            disposable.add(
                repository.getCryptos(_selectedCurrency.value)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> _cryptos.value = result},
                        { error -> Log.e("CryptoVM", "Error: $error") }
                    )
            )
        }

    }

}