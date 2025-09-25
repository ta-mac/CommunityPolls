package com.example.communitypolls.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.communitypolls.ui.ServiceLocator
import com.example.communitypolls.ui.polls.PollListRoute
import com.example.communitypolls.ui.polls.PollSort
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScaffold(
    title: String,
    onSignOut: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                actions = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        actions()
                        TextButton(onClick = onSignOut) { Text("Sign out") }
                    }
                }
            )
        }
    ) { padding -> Box(Modifier.padding(padding).fillMaxSize()) { content() } }
}

/* ------------------------------- Sort menu ------------------------------- */

@Composable
private fun SortMenu(
    sort: PollSort,
    onChange: (PollSort) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Filled.Sort, contentDescription = "Sort")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            @Composable
            fun item(label: String, value: PollSort) {
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = { expanded = false; onChange(value) },
                    trailingIcon = { if (sort == value) Text("•") else null }
                )
            }
            item("Newest", PollSort.NEWEST)
            item("Oldest", PollSort.OLDEST)
            item("A → Z",  PollSort.TITLE_AZ)
            item("Z → A",  PollSort.TITLE_ZA)
        }
    }
}

/* ------------------------------- Guest ------------------------------- */

@Composable
fun HomeGuestScreen(
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onSuggestClick: () -> Unit
) {
    var sort by remember { mutableStateOf(PollSort.NEWEST) }

    HomeScaffold(
        title = "Community Polls",
        onSignOut = onSignOut,
        actions = {
            Button(onClick = onSuggestClick) { Text("Suggest a poll") }
            SortMenu(sort = sort, onChange = { sort = it })
        }
    ) {
        PollListRoute(
            onPollClick = onPollClick,
            showAdminActions = false,
            sort = sort
        )
    }
}

/* -------------------------------- User ------------------------------- */

@Composable
fun HomeUserScreen(
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onSuggestClick: () -> Unit
) {
    var sort by remember { mutableStateOf(PollSort.NEWEST) }

    HomeScaffold(
        title = "Community Polls",
        onSignOut = onSignOut,
        actions = {
            Button(onClick = onSuggestClick) { Text("Suggest a poll") }
            SortMenu(sort = sort, onChange = { sort = it })
        }
    ) {
        PollListRoute(
            onPollClick = onPollClick,
            showAdminActions = false,
            sort = sort
        )
    }
}

/* ------------------------------- Admin ------------------------------- */

@Composable
fun HomeAdminScreen(
    onCreatePoll: () -> Unit,
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onEditPoll: (String) -> Unit,
    onSuggestClick: () -> Unit // opens the review list
) {
    val repo = ServiceLocator.pollRepository
    val scope = rememberCoroutineScope()
    var sort by remember { mutableStateOf(PollSort.NEWEST) }

    var pendingDeleteId by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf(false) }

    HomeScaffold(
        title = "Admin • Community Polls",
        onSignOut = onSignOut,
        actions = {
            Button(onClick = onSuggestClick) { Text("User Poll") }
            Button(onClick = onCreatePoll) { Text("Create poll") }
            SortMenu(sort = sort, onChange = { sort = it })
        }
    ) {
        PollListRoute(
            onPollClick = onPollClick,
            showAdminActions = true,
            onEditPoll = onEditPoll,
            onDeletePoll = { id -> pendingDeleteId = id },
            sort = sort
        )
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
