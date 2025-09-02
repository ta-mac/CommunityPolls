package com.example.communitypolls.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.communitypolls.model.AppUser

@Composable
fun WelcomeScreen(
    loading: Boolean,
    error: String?,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onGuestClick: () -> Unit,
    user: AppUser?,
    onEnter: () -> Unit
) {
    // If a user is already present, auto-continue to the correct home screen
    LaunchedEffect(user) {
        if (user != null && !loading) onEnter()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Community Polls", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onSignInClick,
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Sign in") }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onSignUpClick,
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Sign up") }

        Spacer(Modifier.height(12.dp))

        TextButton(
            onClick = onGuestClick,
            enabled = !loading
        ) { Text("Continue as guest") }

        if (loading) {
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}
