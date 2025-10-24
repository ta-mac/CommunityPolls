@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.communitypolls.ui.sugg

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
<<<<<<< HEAD
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
=======
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
>>>>>>> 71da6fb (Updated App Icon)
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.communitypolls.model.Suggestion

@Composable
fun AdminSuggScreen(
    state: AdminSuggState,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
<<<<<<< HEAD
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Polls (Suggestions)") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
=======
    onBack: () -> Unit,
    onDelete: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Suggestions", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
>>>>>>> 71da6fb (Updated App Icon)
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
<<<<<<< HEAD
            if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.items, key = { it.id }) { s ->
                    SuggestionCard(s, onAccept, onDecline)
=======
            if (state.loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.items, key = { it.id }) { suggestion ->
                    SuggestionCard(
                        suggestion = suggestion,
                        onAccept = onAccept,
                        onDecline = onDecline,
                        onDelete = onDelete
                    )
>>>>>>> 71da6fb (Updated App Icon)
                }
            }
        }
    }
}

@Composable
private fun SuggestionCard(
<<<<<<< HEAD
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
=======
    suggestion: Suggestion,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onDelete: (String) -> Unit //
) {
    val isPending = suggestion.status == "pending"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    suggestion.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                StatusChip(suggestion.status)
            }

            Spacer(Modifier.height(6.dp))
            Text(
                suggestion.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Divider()

            Text(
                "From: ${suggestion.createdByName}",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                if (isPending) {
                    Button(onClick = { onAccept(suggestion.id) }) {
                        Text("Accept")
                    }
                    OutlinedButton(onClick = { onDecline(suggestion.id) }) {
                        Text("Decline")
                    }
                } else {
                    OutlinedButton(
                        onClick = { onDelete(suggestion.id) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                }
>>>>>>> 71da6fb (Updated App Icon)
            }
        }
    }
}

<<<<<<< HEAD
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
=======

@Composable
private fun StatusChip(status: String) {
    val (bgColor, textColor) = when (status) {
        "accepted" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "declined" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "pending" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to Color.Black
    }

    Surface(
        color = bgColor,
        shape = MaterialTheme.shapes.small,
        tonalElevation = 4.dp
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
>>>>>>> 71da6fb (Updated App Icon)
            style = MaterialTheme.typography.labelMedium
        )
    }
}
