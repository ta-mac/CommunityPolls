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
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = onDismissError,
            confirmButton = { TextButton(onClick = onDismissError) { Text("OK") } },
            title = { Text("Problem saving") },
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
        Text("Create Poll", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)

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
            OptionRow(
                index = idx,
                id = option.id,
                text = option.text,
                canRemove = state.options.size > 2,
                onIdChange = { onOptionIdChange(idx, it) },
                onTextChange = { onOptionTextChange(idx, it) },
                onRemove = { onRemoveOption(idx) }
            )
            Spacer(Modifier.height(8.dp))
        }

        TextButton(onClick = onAddOption) { Text("Add option") }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Active")
            Spacer(Modifier.width(12.dp))
            Switch(checked = state.isActive, onCheckedChange = onToggleActive)
        }

        Text("Auto close")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = state.closeAfterHours == null, onClick = { onSelectClosePreset(null) }, label = { Text("Never") })
            FilterChip(selected = state.closeAfterHours == 1, onClick = { onSelectClosePreset(1) }, label = { Text("1h") })
            FilterChip(selected = state.closeAfterHours == 24, onClick = { onSelectClosePreset(24) }, label = { Text("24h") })
            FilterChip(selected = state.closeAfterHours == 72, onClick = { onSelectClosePreset(72) }, label = { Text("3d") })
        }

        Spacer(Modifier.height(12.dp))

        Button(onClick = onSave, enabled = !state.loading) {
            Text(if (state.loading) "Saving…" else "Save")
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
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = id,
            onValueChange = onIdChange,
            label = { Text("ID") },
            singleLine = true,
            modifier = Modifier.weight(0.4f)
        )
        Spacer(Modifier.width(8.dp))
        OutlinedTextField(
            value = text,
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
