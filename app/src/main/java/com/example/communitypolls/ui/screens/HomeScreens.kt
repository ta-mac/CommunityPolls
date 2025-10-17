@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.communitypolls.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.communitypolls.ui.ServiceLocator
import com.example.communitypolls.ui.polls.PollListRoute
import com.example.communitypolls.ui.polls.PollSort
import kotlinx.coroutines.launch

/* ---------------------------- SHARED COMPONENTS ---------------------------- */

@Composable
private fun HomeScaffold(
    onSignOut: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") }, // Remove "Polls" title to avoid duplicate
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        actions()
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = onSignOut) {
                        Text("Sign out", style = MaterialTheme.typography.labelLarge)
                    }
                }
            )
        },
        content = content
    )
}

@Composable
private fun SortBar(
    sort: PollSort,
    onSortChange: (PollSort) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(1.dp, shape = RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { onSortChange(PollSort.NEWEST) }) {
            Icon(Icons.Filled.Sort, contentDescription = "Sort")
        }
    }
}

/* ------------------------------- GUEST ------------------------------- */

@Composable
fun HomeGuestScreen(
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onSuggestClick: () -> Unit
) {
    var sort by remember { mutableStateOf(PollSort.NEWEST) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = onSuggestClick,
                            label = { Text("Suggest") },
                            leadingIcon = {
                                Icon(Icons.Default.Add, contentDescription = null)
                            },
                            shape = RoundedCornerShape(50)
                        )
                        IconButton(onClick = {
                            sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST
                        }) {
                            Icon(Icons.Filled.Sort, contentDescription = "Sort")
                        }
                        TextButton(onClick = onSignOut) {
                            Text("Sign out", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            Text(
                text = "Polls",
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleLarge
            )

            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = sort
            )
        }
    }
}



/* ------------------------------- USER ------------------------------- */

@Composable
fun HomeUserScreen(
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onSuggestClick: () -> Unit
) {
    var sort by remember { mutableStateOf(PollSort.NEWEST) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = onSuggestClick,
                            label = { Text("Suggest") },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                            shape = RoundedCornerShape(50)
                        )
                        TextButton(onClick = onSignOut) {
                            Text("Sign out", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            // Sorting Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(1.dp, shape = RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Active", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.width(8.dp))
                    Text("•", color = Color.Green)
                    Spacer(Modifier.width(8.dp))
                    Text("2 Options", fontWeight = FontWeight.Light)
                }
                IconButton(onClick = {
                    sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST
                }) {
                    Icon(Icons.Default.Sort, contentDescription = "Sort")
                }
            }

            Text(
                text = "Polls",
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleLarge
            )

            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = sort
            )
        }
    }
}


/* ------------------------------- ADMIN ------------------------------- */

@Composable
fun HomeAdminScreen(
    onCreatePoll: () -> Unit,
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onEditPoll: (String) -> Unit,
    onSuggestClick: () -> Unit
) {
    val repo = ServiceLocator.pollRepository
    val scope = rememberCoroutineScope()
    var sort by remember { mutableStateOf(PollSort.NEWEST) }
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = onSuggestClick,
                            label = { Text("User Polls") },
                            shape = RoundedCornerShape(50)
                        )
                        AssistChip(
                            onClick = onCreatePoll,
                            label = { Text("Create") },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = "Create") },
                            shape = RoundedCornerShape(50)
                        )
                        TextButton(onClick = onSignOut) {
                            Text("Sign out", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            // Sorting Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(1.dp, shape = RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Active", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.width(8.dp))
                    Text("•", color = Color.Green)
                    Spacer(Modifier.width(8.dp))
                    Text("2 Options", fontWeight = FontWeight.Light)
                }
                IconButton(onClick = {
                    sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST
                }) {
                    Icon(Icons.Default.Sort, contentDescription = "Sort")
                }
            }

            // Section Title
            Text(
                text = "Polls",
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleLarge
            )

            // Poll List
            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = true,
                onEditPoll = onEditPoll,
                onDeletePoll = { id -> pendingDeleteId = id },
                sort = sort
            )
        }
    }

    // Confirm Delete Dialog
    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { if (!deleting) pendingDeleteId = null },
            confirmButton = {
                TextButton(
                    enabled = !deleting,
                    onClick = {
                        val id = pendingDeleteId ?: return@TextButton
                        scope.launch {
                            deleting = true
                            try {
                                repo.deletePoll(id)
                                pendingDeleteId = null
                            } finally {
                                deleting = false
                            }
                        }
                    }
                ) {
                    Text(if (deleting) "Deleting…" else "Delete")
                }
            },
            dismissButton = {
                TextButton(enabled = !deleting, onClick = { pendingDeleteId = null }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete poll") },
            text = { Text("Are you sure you want to delete this poll? This cannot be undone.") }
        )
    }
}

