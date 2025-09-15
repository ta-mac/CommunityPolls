package com.example.communitypolls.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.communitypolls.ui.polls.PollListRoute
import com.example.communitypolls.ui.ServiceLocator
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

/* ------------------------------- Guest ------------------------------- */

@Composable
fun HomeGuestScreen(
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit
) {
    HomeScaffold(title = "Community Polls", onSignOut = onSignOut) {
        PollListRoute(onPollClick = onPollClick, showAdminActions = false)
    }
}

/* -------------------------------- User ------------------------------- */

@Composable
fun HomeUserScreen(
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit
) {
    HomeScaffold(title = "Community Polls", onSignOut = onSignOut) {
        PollListRoute(onPollClick = onPollClick, showAdminActions = false)
    }
}

/* ------------------------------- Admin ------------------------------- */

@Composable
fun HomeAdminScreen(
    onCreatePoll: () -> Unit,
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onEditPoll: (String) -> Unit
) {
    val repo = ServiceLocator.pollRepository
    val scope = rememberCoroutineScope()

    var pendingDeleteId by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf(false) }

    HomeScaffold(
        title = "Admin • Community Polls",
        onSignOut = onSignOut,
        actions = { Button(onClick = onCreatePoll) { Text("Create poll") } }
    ) {
        PollListRoute(
            onPollClick = onPollClick,
            showAdminActions = true,
            onEditPoll = onEditPoll,
            onDeletePoll = { id -> pendingDeleteId = id }
        )
    }

    // Confirm delete dialog
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
                                // Perform delete; ignore returned result for now
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
