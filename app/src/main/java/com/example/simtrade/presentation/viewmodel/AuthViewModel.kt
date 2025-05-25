package com.example.simtrade.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.simtrade.data.model.UserCredentials
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var credentials by mutableStateOf(UserCredentials())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isLoggedIn by mutableStateOf(true)

    fun login(){
        val email = credentials.email.trim()
        var password = credentials.password

        if(credentials.email.isBlank() || credentials.password.isBlank()){
            errorMessage = "Email and password cannot be empty"
            return
        }

        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful){
                    isLoggedIn = true
                } else
                    errorMessage = task.exception?.localizedMessage?: "Login failed"
            }
    }
}