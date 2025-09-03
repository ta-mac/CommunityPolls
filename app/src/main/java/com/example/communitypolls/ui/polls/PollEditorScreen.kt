package com.example.communitypolls.ui.polls

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollEditorScreen(
    state: PollEditorUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onOptionIdChange: (index: Int, value: String) -> Unit,
    onOptionTextChange: (index: Int, value: String) -> Unit,
    onAddOption: () -> Unit,
    onRemoveOption: (index: Int) -> Unit,
    onToggleActive: (Boolean) -> Unit,
    onSelectClosePreset: (Long?) -> Unit,
    onSave: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
    screenTitle: String = "Create Poll",
    primaryButtonText: String = "Save"
) {
    val scroll = rememberScrollState()

    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(screenTitle) },
            colors = TopAppBarDefaults.topAppBarColors()
        )

        if (state.loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(scroll)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text("Options", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            state.options.forEachIndexed { index, opt ->
                PollEditorOptionRow(
                    option = opt,
                    canRemove = state.options.size > 2,
                    onIdChange = { onOptionIdChange(index, it) },
                    onTextChange = { onOptionTextChange(index, it) },
                    onRemove = { onRemoveOption(index) }
                )
                Spacer(Modifier.height(8.dp))
            }

            OutlinedButton(onClick = onAddOption) { Text("Add option") }

            Spacer(Modifier.height(24.dp))

            Text("Status & Schedule", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            // Active switch
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Active", modifier = Modifier.weight(1f))
                Switch(checked = state.isActive, onCheckedChange = onToggleActive)
            }

            Spacer(Modifier.height(12.dp))

            Text("Closes", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { onSelectClosePreset(null) }) { Text("No close") }
                OutlinedButton(onClick = { onSelectClosePreset(6) }) { Text("6h") }
                OutlinedButton(onClick = { onSelectClosePreset(24) }) { Text("1d") }
                OutlinedButton(onClick = { onSelectClosePreset(72) }) { Text("3d") }
                OutlinedButton(onClick = { onSelectClosePreset(168) }) { Text("7d") }
            }

            val closeLabel =
                if (state.closesAt == null) "No closing time"
                else "Closes at: " + java.text.DateFormat.getDateTimeInstance()
                    .format(java.util.Date(state.closesAt))
            Spacer(Modifier.height(6.dp))
            Text(closeLabel, style = MaterialTheme.typography.bodySmall)
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onDismissError,
                enabled = !state.loading
            ) { Text("Cancel") }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onSave,
                enabled = !state.loading
            ) { Text(primaryButtonText) }
        }
    }

    if (state.error != null) {
        Dialog(
            onDismissRequest = onDismissError,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Card(
                colors = CardDefaults.cardColors(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Problem saving", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(state.error)
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(onClick = onDismissError) { Text("OK") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PollEditorOptionRow(
    option: PollEditorOption,
    canRemove: Boolean,
    onIdChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = option.id,
            onValueChange = onIdChange,
            label = { Text("Id") },
            singleLine = true,
            modifier = Modifier.weight(0.4f)
        )
        Spacer(Modifier.width(8.dp))
        OutlinedTextField(
            value = option.text,
            onValueChange = onTextChange,
            label = { Text("Text") },
            singleLine = true,
            modifier = Modifier.weight(0.6f)
        )
        if (canRemove) {
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove option")
            }
        }
    }
}
