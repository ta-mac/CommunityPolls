package com.example.communitypolls.ui.polls

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollEditorScreen(
    state: PollEditorState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onOptionIdChange: (index: Int, value: String) -> Unit,
    onOptionTextChange: (index: Int, value: String) -> Unit,
    onAddOption: () -> Unit,
    onRemoveOption: (index: Int) -> Unit,
    onSave: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()

    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Create Poll") },
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
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text("Description (optional)") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            Text("Options", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            state.options.forEachIndexed { index, opt ->
                OptionCard(
                    index = index,
                    id = opt.id,
                    text = opt.text,
                    onIdChange = { onOptionIdChange(index, it) },
                    onTextChange = { onOptionTextChange(index, it) },
                    onRemove = { onRemoveOption(index) },
                    canRemove = state.options.size > 2
                )
                Spacer(Modifier.height(8.dp))
            }

            OutlinedButton(onClick = onAddOption) {
                Text("Add option")
            }

            Spacer(Modifier.height(32.dp))
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
            ) { Text("Save") }
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
                    Text("Problem saving", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Text(state.error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onDismissError, modifier = Modifier.align(Alignment.End)) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionCard(
    index: Int,
    id: String,
    text: String,
    onIdChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = id,
                    onValueChange = onIdChange,
                    label = { Text("Option id #${index + 1}") },
                    singleLine = true,
                    modifier = Modifier.weight(0.4f)
                )
                Spacer(Modifier.width(12.dp))
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
    }
}
