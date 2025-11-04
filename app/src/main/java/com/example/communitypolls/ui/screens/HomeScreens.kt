@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.communitypolls.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.communitypolls.ui.ServiceLocator
import com.example.communitypolls.ui.polls.PollListRoute
import com.example.communitypolls.ui.polls.PollSort
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

/* ---------------------------- SHARED TOP BAR ---------------------------- */

@Composable
private fun PollsTopBar(
    navController: NavController,
    onSuggestClick: () -> Unit,
    onSortClick: () -> Unit,
    sort: PollSort,
    displayName: String?,
    email: String?,
    showSort: Boolean = true,
    suggestLabel: String = "Suggest"
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                if (!displayName.isNullOrBlank()) {
                    Text(text = displayName, style = MaterialTheme.typography.titleMedium)
                }
                if (!email.isNullOrBlank()) {
                    Text(text = email, style = MaterialTheme.typography.bodySmall)
                }
            }

            IconButton(onClick = { navController.navigate("profile") }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AssistChip(
                onClick = onSuggestClick,
                label = { Text(suggestLabel) },
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                shape = RoundedCornerShape(50)
            )
            if (showSort) {
                AssistChip(
                    onClick = onSortClick,
                    label = { Text("Sort: ${if (sort == PollSort.NEWEST) "Newest" else "Oldest"}") },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )
            }
        }
    }
}

/* ------------------------------- GUEST ------------------------------- */

@Composable
fun HomeGuestScreen(
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit
) {
    var sort by remember { mutableStateOf(PollSort.NEWEST) }

    Scaffold(
        topBar = {
            // Keep only sort control for guests (no Suggest)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {
                        sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST
                    },
                    label = { Text("Sort: ${if (sort == PollSort.NEWEST) "Newest" else "Oldest"}") },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onSignOut) {
                    Text("Sign out", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // onPollClick for guests should be routed to view results only
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
    navController: NavHostController,
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onSuggestClick: () -> Unit,
    displayName: String?,
    email: String?,
    onProfileClick: () -> Unit
) {
    var sort by remember { mutableStateOf(PollSort.NEWEST) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        displayName?.let {
                            Text(it, style = MaterialTheme.typography.titleSmall)
                        }
                        email?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    IconButton(onClick = onProfileClick) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {
                            sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST
                        },
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

            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onSignOut) {
                    Text("Sign out", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = onSuggestClick,
                    label = { Text("User Polls") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )

                Spacer(modifier = Modifier.weight(1f))

                AssistChip(
                    onClick = onCreatePoll,
                    label = { Text("Create Poll") },
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )

                Spacer(modifier = Modifier.weight(1f))

                AssistChip(
                    onClick = {
                        sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST
                    },
                    label = { Text("Sort: ${if (sort == PollSort.NEWEST) "New" else "Old"}") },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onSignOut) {
                    Text("Sign out", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = true,
                onEditPoll = onEditPoll,
                onDeletePoll = { id -> pendingDeleteId = id },
                sort = sort
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
                            } finally {
                                deleting = false
                            }
                        }
                    }
                ) { Text(if (deleting) "Deleting…" else "Delete") }
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
