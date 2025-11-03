package com.example.communitypolls.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.communitypolls.model.AppUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: AppUser?,
    onUpdateName: (String) -> Unit,
    onChangePassword: (String) -> Unit,
    error: String?,
    loading: Boolean
) {
    var name by remember { mutableStateOf(user?.displayName ?: "") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Email: ${user?.email ?: "Unknown"}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onUpdateName(name) },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("Update Name")
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("New Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onChangePassword(password) },
                modifier = Modifier.fillMaxWidth(),
                enabled = password.length >= 6
            ) {
                Text("Change Password")
            }

            if (loading) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = if (it.contains("updated")) Color.Green else Color.Red)
            }
        }
    }
}
