package com.example.communitypolls.ui.screens

import androidx.compose.foundation.layout.*
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> 5f6ea81 (Updated App Icon)
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
<<<<<<< HEAD
=======
>>>>>>> 0af30b8 (Added some security measures)
=======
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
<<<<<<< HEAD
<<<<<<< HEAD
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
=======
import androidx.compose.ui.unit.dp

>>>>>>> 0af30b8 (Added some security measures)
=======
import androidx.compose.ui.unit.dp

=======
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
@Composable
fun SignUpScreen(
    loading: Boolean,
    error: String?,
    onSubmit: (String, String, String) -> Unit, // email, password, displayName
    onGoToSignIn: () -> Unit = {}
) {
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
<<<<<<< HEAD
<<<<<<< HEAD
    var passwordVisible by remember { mutableStateOf(false) }

    val canSubmit = displayName.isNotBlank() && email.isNotBlank() && password.length >= 6

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Account", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display Name input
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Display name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Email input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Password input with visibility toggle
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (min 6 chars)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = "Toggle password visibility")
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Submit button
            Button(
                onClick = { onSubmit(email.trim(), password, displayName.trim()) },
                enabled = canSubmit && !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Create account")
            }

            // Link to sign in
            TextButton(
                onClick = onGoToSignIn,
                modifier = Modifier.padding(top = 8.dp),
                enabled = !loading
            ) {
                Text("I already have an account")
            }

            // Loading and error feedback
            if (loading) {
                Spacer(Modifier.height(24.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (error != null) {
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
=======
=======
>>>>>>> 5f6ea81 (Updated App Icon)

    val canSubmit = displayName.isNotBlank() && email.isNotBlank() && password.length >= 6

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Create account", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Display name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (min 6 chars)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onSubmit(email.trim(), password, displayName.trim()) },
            enabled = canSubmit && !loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Create account") }

        TextButton(
            onClick = onGoToSignIn,
            enabled = !loading
        ) { Text("I already have an account") }

        if (loading) {
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
<<<<<<< HEAD
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
    var passwordVisible by remember { mutableStateOf(false) }

    val canSubmit = displayName.isNotBlank() && email.isNotBlank() && password.length >= 6

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Account", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display Name input
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Display name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Email input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Password input with visibility toggle
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (min 6 chars)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = "Toggle password visibility")
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Submit button
            Button(
                onClick = { onSubmit(email.trim(), password, displayName.trim()) },
                enabled = canSubmit && !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Create account")
            }

            // Link to sign in
            TextButton(
                onClick = onGoToSignIn,
                modifier = Modifier.padding(top = 8.dp),
                enabled = !loading
            ) {
                Text("I already have an account")
            }

            // Loading and error feedback
            if (loading) {
                Spacer(Modifier.height(24.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (error != null) {
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
        }
    }
}
