package com.example.simtrade.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.simtrade.presentation.viewmodel.AuthViewModel
import org.w3c.dom.Text
import java.util.concurrent.atomic.AtomicLongArray

@Composable
fun LoginScreen(viewModel: AuthViewModel, onLoginSuccess: () -> Unit) {

    if (viewModel.isLoggedIn){
        LaunchedEffect(Unit) {
            onLoginSuccess()
        }
    }

    Column(modifier = Modifier.fillMaxSize()
        .padding(24.dp),
        verticalArrangement = Arrangement.Center) {

        TextField(value = viewModel.credentials.email,
            onValueChange = {newEmail ->
                viewModel.credentials = viewModel.credentials.copy(email = newEmail)
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.padding(12.dp))

        TextField(value = viewModel.credentials.password,
            onValueChange = {newPassword ->
                viewModel.credentials = viewModel.credentials.copy(password = newPassword)
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.padding(16.dp))

        if(viewModel.isLoading){
            CircularProgressIndicator()
        } else {
            Button(onClick = {viewModel.login() },
                    modifier = Modifier.fillMaxWidth()) {
                Text("Login")
            }
        }

        viewModel.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = error, color = Color.Red)
        }

    }
}