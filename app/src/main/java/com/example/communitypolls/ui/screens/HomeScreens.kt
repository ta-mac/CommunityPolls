@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.communitypolls.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.communitypolls.ui.ServiceLocator
import com.example.communitypolls.ui.polls.PollListRoute
import com.example.communitypolls.ui.polls.PollSort
import kotlinx.coroutines.launch

/* ------------------------------- GUEST ------------------------------- */

@Composable
fun HomeGuestScreen(
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit
) {
    var sort by remember { mutableStateOf(PollSort.NEWEST) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                // Sort row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST },
                        label = { Text("Sort: ${if (sort == PollSort.NEWEST) "Newest" else "Oldest"}") },
                        leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                        shape = RoundedCornerShape(50)
                    )
                }

                Spacer(Modifier.height(6.dp))

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                    placeholder = {
                        Text(
                            "Search polls by title or description",
                            style = TextStyle(fontSize = 13.sp) // smaller text
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large
                )
            }
        },
        bottomBar = {
            Box(
                Modifier.fillMaxWidth().padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onSignOut) {
                    Text("Sign out", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = sort,
                searchQuery = searchQuery
            )
        }
    }
}

/* ------------------------------- USER ------------------------------- */

@Composable
fun HomeUserScreen(
    navController: NavHostController,
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onSuggestClick: () -> Unit,
    displayName: String?,
    email: String?,
    onProfileClick: () -> Unit
) {
    var sort by remember { mutableStateOf(PollSort.NEWEST) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // User info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        displayName?.let { Text(it, style = MaterialTheme.typography.titleSmall) }
                        email?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(28.dp))
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Sort (left) & Suggest (right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST },
                        label = { Text("Sort: ${if (sort == PollSort.NEWEST) "Newest" else "Oldest"}") },
                        leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                        shape = RoundedCornerShape(50)
                    )

                    AssistChip(
                        onClick = onSuggestClick,
                        label = { Text("Suggest") },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                        shape = RoundedCornerShape(50)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                    placeholder = {
                        Text(
                            "Search polls by title or description",
                            style = TextStyle(fontSize = 13.sp) // smaller text
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large
                )
            }
        },
        bottomBar = {
            Box(
                Modifier.fillMaxWidth().padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onSignOut) {
                    Text("Sign out", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = sort,
                searchQuery = searchQuery
            )
        }
    }
}

/* ------------------------------- ADMIN ------------------------------- */

@Composable
fun HomeAdminScreen(
    navController: NavHostController,
    onCreatePoll: () -> Unit,
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onEditPoll: (String) -> Unit,
    onSuggestClick: () -> Unit
) {
    val repo = ServiceLocator.pollRepository
    val scope = rememberCoroutineScope()
    var sort by remember { mutableStateOf(PollSort.NEWEST) }
    var searchQuery by remember { mutableStateOf("") }
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST },
                        label = { Text("Sort: ${if (sort == PollSort.NEWEST) "Newest" else "Oldest"}") },
                        leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                        shape = RoundedCornerShape(50)
                    )
                    AssistChip(
                        onClick = onSuggestClick,
                        label = { Text("User Polls") },
                        leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                        shape = RoundedCornerShape(50)
                    )
                    AssistChip(
                        onClick = onCreatePoll,
                        label = { Text("Create") },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                        shape = RoundedCornerShape(50)
                    )
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                    placeholder = {
                        Text("Search polls by title or description", style = TextStyle(fontSize = 13.sp))
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large
                )
            }
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth().padding(bottom = 12.dp), contentAlignment = Alignment.Center) {
                TextButton(onClick = onSignOut) { Text("Sign out", style = MaterialTheme.typography.labelLarge) }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = true,
                onEditPoll = onEditPoll,
                onDeletePoll = { id -> pendingDeleteId = id },
                sort = sort,
                searchQuery = searchQuery,
                onViewVotes = { pollId, pollTitle ->
                    navController.navigate("voteList/$pollId/$pollTitle")
                }
            )
        }
    }

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
                            } finally { deleting = false }
                        }
                    }
                ) { Text(if (deleting) "Deleting…" else "Delete") }
            },
            dismissButton = {
                TextButton(enabled = !deleting, onClick = { pendingDeleteId = null }) { Text("Cancel") }
            },
            title = { Text("Delete poll") },
            text = { Text("Are you sure you want to delete this poll? This cannot be undone.") }
        )
    }
}
