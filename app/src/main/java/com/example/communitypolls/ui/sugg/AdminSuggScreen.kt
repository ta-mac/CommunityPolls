@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.communitypolls.ui.sugg

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.communitypolls.model.Suggestion

@Composable
fun AdminSuggScreen(
    state: AdminSuggState,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Polls (Suggestions)") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.items, key = { it.id }) { s ->
                    SuggestionCard(s, onAccept, onDecline)
                }
            }
        }
    }
}

@Composable
private fun SuggestionCard(
    s: Suggestion,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit
) {
    val pending = s.status == "pending"
    Card {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(s.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                StatusChip(s.status)
            }
            Text(s.description)
            Divider()
            Text("From: ${s.createdByName}", style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(enabled = pending, onClick = { onAccept(s.id) }) { Text("Accept") }
                OutlinedButton(enabled = pending, onClick = { onDecline(s.id) }) { Text("Decline") }
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val color = when (status) {
        "pending" -> MaterialTheme.colorScheme.secondaryContainer
        "accepted" -> MaterialTheme.colorScheme.primaryContainer
        "declined" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Surface(color = color, tonalElevation = 2.dp, shape = MaterialTheme.shapes.small) {
        Text(
            status.replaceFirstChar { it.titlecase() },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
