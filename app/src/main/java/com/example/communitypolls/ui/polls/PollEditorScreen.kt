package com.example.communitypolls.ui.polls

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
<<<<<<< HEAD
<<<<<<< HEAD
import androidx.compose.material.icons.filled.ArrowBack
=======
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
import androidx.compose.material.icons.filled.ArrowBack
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

<<<<<<< HEAD
<<<<<<< HEAD
@OptIn(ExperimentalMaterial3Api::class)
=======
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
@OptIn(ExperimentalMaterial3Api::class)
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
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
<<<<<<< HEAD
<<<<<<< HEAD
    // Show error dialog if present
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = onDismissError,
            confirmButton = {
                TextButton(onClick = onDismissError) { Text("OK") }
            },
=======
=======
>>>>>>> 5f6ea81 (Updated App Icon)
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = onDismissError,
            confirmButton = { TextButton(onClick = onDismissError) { Text("OK") } },
<<<<<<< HEAD
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
    // Show error dialog if present
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = onDismissError,
            confirmButton = {
                TextButton(onClick = onDismissError) { Text("OK") }
            },
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
            title = { Text("Problem saving") },
            text = { Text(state.error) }
        )
    }

<<<<<<< HEAD
<<<<<<< HEAD
    // Scaffold structure with TopAppBar to fix cut-off title issue
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
            // Better input layout with Material 3 spacing
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

            // Wrap each option input in a Card for better visual grouping
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

            TextButton(onClick = onAddOption) {
                Text("Add option")
            }

            // Active toggle with aligned label
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Active")
                Spacer(Modifier.width(12.dp))
                Switch(checked = state.isActive, onCheckedChange = onToggleActive)
            }

            // Better auto-close presets with spacing
            Column {
                Text("Auto close")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = state.closeAfterHours == null, onClick = { onSelectClosePreset(null) }, label = { Text("Never") })
                    FilterChip(selected = state.closeAfterHours == 1, onClick = { onSelectClosePreset(1) }, label = { Text("1h") })
                    FilterChip(selected = state.closeAfterHours == 24, onClick = { onSelectClosePreset(24) }, label = { Text("24h") })
                    FilterChip(selected = state.closeAfterHours == 72, onClick = { onSelectClosePreset(72) }, label = { Text("3d") })
                }
            }

            // Full-width primary button for Save
            Button(
                onClick = onSave,
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.loading) "Saving…" else "Save")
            }
=======
=======
>>>>>>> 5f6ea81 (Updated App Icon)
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
<<<<<<< HEAD
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
    // Scaffold structure with TopAppBar to fix cut-off title issue
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
            // Better input layout with Material 3 spacing
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

            // Wrap each option input in a Card for better visual grouping
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

            TextButton(onClick = onAddOption) {
                Text("Add option")
            }

            // Active toggle with aligned label
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Active")
                Spacer(Modifier.width(12.dp))
                Switch(checked = state.isActive, onCheckedChange = onToggleActive)
            }

            // Better auto-close presets with spacing
            Column {
                Text("Auto close")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = state.closeAfterHours == null, onClick = { onSelectClosePreset(null) }, label = { Text("Never") })
                    FilterChip(selected = state.closeAfterHours == 1, onClick = { onSelectClosePreset(1) }, label = { Text("1h") })
                    FilterChip(selected = state.closeAfterHours == 24, onClick = { onSelectClosePreset(24) }, label = { Text("24h") })
                    FilterChip(selected = state.closeAfterHours == 72, onClick = { onSelectClosePreset(72) }, label = { Text("3d") })
                }
            }

            // Full-width primary button for Save
            Button(
                onClick = onSave,
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.loading) "Saving…" else "Save")
            }
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
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
<<<<<<< HEAD
<<<<<<< HEAD
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Single-column layout instead of cramped row
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
=======
=======
>>>>>>> 5f6ea81 (Updated App Icon)
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
<<<<<<< HEAD
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Single-column layout instead of cramped row
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
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
            }
        }
    }
}
