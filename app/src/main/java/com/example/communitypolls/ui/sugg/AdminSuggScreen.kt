@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.communitypolls.ui.sugg

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            CenterAlignedTopAppBar(
                title = { Text("Suggestions", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
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
                        onDecline = onDecline
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionCard(
    suggestion: Suggestion,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit
) {
    val isPending = suggestion.status == "pending"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
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
                Button(
                    onClick = { onAccept(suggestion.id) },
                    enabled = isPending,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Accept")
                }
                OutlinedButton(
                    onClick = { onDecline(suggestion.id) },
                    enabled = isPending,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Decline")
                }
            }
        }
    }
}

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
            style = MaterialTheme.typography.labelMedium
        )
    }
}
