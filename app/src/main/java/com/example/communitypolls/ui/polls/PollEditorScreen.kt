package com.example.communitypolls.ui.polls

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollEditorScreen(
    state: PollEditorState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onOptionIdChange: (Int, String) -> Unit,
    onOptionTextChange: (Int, String) -> Unit,
    onAddOption: () -> Unit,
    onRemoveOption: (Int) -> Unit,
    onToggleActive: (Boolean) -> Unit,
    onSelectClosePreset: (Int?) -> Unit,
    onSave: () -> Unit,
    onDismissError: () -> Unit,
) {
    // Show error dialog if present
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = onDismissError,
            confirmButton = { TextButton(onClick = onDismissError) { Text("OK") } },
            title = { Text("Problem saving") },
            text = { Text(state.error) }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Poll", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onDismissError) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Options", style = MaterialTheme.typography.titleMedium)

            state.options.forEachIndexed { idx, option ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OptionRow(
                            index = idx,
                            id = option.id,
                            text = option.text,
                            canRemove = state.options.size > 2,
                            onIdChange = { onOptionIdChange(idx, it) },
                            onTextChange = { onOptionTextChange(idx, it) },
                            onRemove = { onRemoveOption(idx) }
                        )
                    }
                }
            }

            TextButton(onClick = onAddOption) { Text("Add option") }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Active")
                Spacer(Modifier.width(12.dp))
                Switch(checked = state.isActive, onCheckedChange = onToggleActive)
            }

            Column {
                Text("Auto close")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = state.closeAfterHours == null,
                        onClick = { onSelectClosePreset(null) },
                        label = { Text("Never") }
                    )
                    FilterChip(
                        selected = state.closeAfterHours == 1,
                        onClick = { onSelectClosePreset(1) },
                        label = { Text("1h") }
                    )
                    FilterChip(
                        selected = state.closeAfterHours == 24,
                        onClick = { onSelectClosePreset(24) },
                        label = { Text("24h") }
                    )
                    FilterChip(
                        selected = state.closeAfterHours == 72,
                        onClick = { onSelectClosePreset(72) },
                        label = { Text("3d") }
                    )
                }
            }

            Button(
                onClick = onSave,
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.loading) "Saving…" else "Save")
            }
        }
    }
}

@Composable
private fun OptionRow(
    index: Int,
    id: String,
    text: String,
    canRemove: Boolean,
    onIdChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = id,
            onValueChange = onIdChange,
            label = { Text("Option ID") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text("Option Text") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (canRemove) {
            TextButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove option")
                Spacer(Modifier.width(4.dp))
                Text("Remove option")
            }
        }
    }
}
