package com.example.communitypolls.ui.sugg

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SuggestPollScreen(
    state: SuggestPollState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onNameOrEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismissError: () -> Unit
) {
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = onDismissError,
            confirmButton = { TextButton(onClick = onDismissError) { Text("OK") } },
            title = { Text("Problem submitting") },
            text = { Text(state.error) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Suggest a Poll", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
        )

        OutlinedTextField(
            value = state.yourNameOrEmail,
            onValueChange = onNameOrEmailChange,
            label = { Text("Your name or email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onSubmit,
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.loading) "Submitting…" else "Submit Suggestion")
        }
    }
}
